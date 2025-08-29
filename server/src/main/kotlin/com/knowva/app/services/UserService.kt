package com.knowva.app.services

import com.knowva.app.database.*
import com.knowva.app.models.*
import com.knowva.app.exceptions.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*
import java.time.Instant
import java.time.temporal.ChronoUnit

// User Service
class UserService(
    private val jwtService: JwtService,
    private val passwordService: PasswordService
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun registerUser(
        request: RegisterRequest,
        ipAddress: String?,
        userAgent: String?
    ): AuthResponse = newSuspendedTransaction {
        // Validate input
        val passwordErrors = passwordService.validatePasswordStrength(request.password)
        if (passwordErrors.isNotEmpty()) {
            throw ValidationException(
                "Password validation failed",
                passwordErrors.joinToString("; ")
            )
        }

        if (!isValidEmail(request.email)) {
            throw ValidationException("Invalid email format")
        }

        if (!isValidUsername(request.username)) {
            throw ValidationException("Username must be 3-50 characters and contain only letters, numbers, and underscores")
        }

        // Check if user exists
        val existingUser = Users.selectAll()
            .where { (Users.email eq request.email.lowercase()) or (Users.username eq request.username.lowercase()) }
            .firstOrNull()

        if (existingUser != null) {
            val field =
                if (existingUser[Users.email] == request.email.lowercase()) "email" else "username"
            throw ConflictException("User with this $field already exists")
        }

        // Create user
        val userId = Users.insert {
            it[username] = request.username.lowercase()
            it[displayName] = request.displayName
            it[email] = request.email.lowercase()
            it[passwordHash] = passwordService.hashPassword(request.password)
            it[preferences] = json.encodeToString(UserPreferences())
        } get Users.id

        // Create session
        val sessionId = createUserSession(userId, ipAddress, userAgent)

        // Generate tokens
        val accessToken = jwtService.generateAccessToken(userId.toString(), sessionId)
        val refreshToken = jwtService.generateRefreshToken()

        // Store refresh token
        RefreshTokens.insert {
            it[RefreshTokens.userId] = userId
            it[token] = refreshToken
            it[expiresAt] = Instant.now().plus(30, ChronoUnit.DAYS)
        }

        // Get user profile
        val userProfile = getUserProfileById(userId.toString())

        AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = userProfile,
            expiresIn = 30 * 60 * 1000 // 30 minutes
        )
    }

    suspend fun loginUser(
        request: LoginRequest,
        ipAddress: String?,
        userAgent: String?
    ): AuthResponse = newSuspendedTransaction {
        val user = Users.selectAll()
            .where { Users.email eq request.email.lowercase() }
            .firstOrNull()
            ?: throw UnauthorizedException("Invalid email or password")

        if (!passwordService.verifyPassword(request.password, user[Users.passwordHash])) {
            throw UnauthorizedException("Invalid email or password")
        }

        if (!user[Users.isActive]) {
            throw UnauthorizedException("Account is deactivated")
        }

        val userId = user[Users.id]

        // Create new session
        val sessionId = createUserSession(userId, ipAddress, userAgent)

        // Generate tokens
        val accessToken = jwtService.generateAccessToken(userId.toString(), sessionId)
        val refreshToken = jwtService.generateRefreshToken()

        // Store refresh token (revoke old ones)
        RefreshTokens.update({ RefreshTokens.userId eq userId }) {
            it[isRevoked] = true
        }

        RefreshTokens.insert {
            it[RefreshTokens.userId] = userId
            it[token] = refreshToken
            it[expiresAt] = Instant.now().plus(30, ChronoUnit.DAYS)
        }

        // Update last active
        Users.update({ Users.id eq userId }) {
            it[lastActiveAt] = Instant.now()
        }

        val userProfile = getUserProfileById(userId.toString())

        AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = userProfile,
            expiresIn = 30 * 60 * 1000
        )
    }

    suspend fun refreshToken(request: RefreshTokenRequest): AuthResponse = newSuspendedTransaction {
        val tokenRecord = RefreshTokens.join(Users, JoinType.INNER, RefreshTokens.userId, Users.id)
            .selectAll()
            .where {
                (RefreshTokens.token eq request.refreshToken) and
                        (RefreshTokens.isRevoked eq false) and
                        (RefreshTokens.expiresAt greater Instant.now()) and
                        (Users.isActive eq true)
            }
            .firstOrNull()
            ?: throw UnauthorizedException("Invalid or expired refresh token")

        val userId = tokenRecord[Users.id]
        val sessionId = UUID.randomUUID().toString()

        // Generate new tokens
        val accessToken = jwtService.generateAccessToken(userId.toString(), sessionId)
        val newRefreshToken = jwtService.generateRefreshToken()

        // Revoke old refresh token and create new one
        RefreshTokens.update({ RefreshTokens.token eq request.refreshToken }) {
            it[isRevoked] = true
        }

        RefreshTokens.insert {
            it[RefreshTokens.userId] = userId
            it[token] = newRefreshToken
            it[expiresAt] = Instant.now().plus(30, ChronoUnit.DAYS)
        }

        val userProfile = getUserProfileById(userId.toString())

        AuthResponse(
            accessToken = accessToken,
            refreshToken = newRefreshToken,
            user = userProfile,
            expiresIn = 30 * 60 * 1000
        )
    }

    suspend fun getUserProfileById(userId: String): UserProfileResponse = newSuspendedTransaction {
        val user = Users.selectAll()
            .where { Users.id eq UUID.fromString(userId) }
            .firstOrNull()
            ?: throw NotFoundException("User not found")

        val preferences = try {
            json.decodeFromString<UserPreferences>(user[Users.preferences])
        } catch (e: Exception) {
            UserPreferences()
        }

        val badges = try {
            json.decodeFromString<List<String>>(user[Users.badges])
        } catch (e: Exception) {
            emptyList()
        }

        val winRate = if (user[Users.gamesPlayed] > 0) {
            (user[Users.gamesWon].toDouble() / user[Users.gamesPlayed].toDouble()) * 100
        } else 0.0

        val rank = calculateUserRank(user[Users.level], winRate)

        UserProfileResponse(
            id = user[Users.id].toString(),
            username = user[Users.username],
            displayName = user[Users.displayName],
            email = user[Users.email],
            avatarUrl = user[Users.avatarUrl],
            level = user[Users.level],
            totalScore = user[Users.totalScore],
            gamesPlayed = user[Users.gamesPlayed],
            gamesWon = user[Users.gamesWon],
            winRate = String.format("%.1f", winRate).toDouble(),
            rank = rank,
            badges = badges,
            preferences = preferences,
            createdAt = user[Users.createdAt].toString(),
            lastActiveAt = user[Users.lastActiveAt].toString(),
            isOnline = isUserOnline(userId)
        )
    }

    suspend fun updateUserProfile(
        userId: String,
        request: UpdateProfileRequest
    ): UserProfileResponse = newSuspendedTransaction {
        Users.update({ Users.id eq UUID.fromString(userId) }) {
            request.displayName?.let { name ->
                if (name.length in 1..100) {
                    it[displayName] = name
                } else {
                    throw ValidationException("Display name must be between 1 and 100 characters")
                }
            }
            request.avatarUrl?.let { url ->
                if (isValidUrl(url)) {
                    it[avatarUrl] = url
                } else {
                    throw ValidationException("Invalid avatar URL format")
                }
            }
            request.preferences?.let { prefs ->
                it[preferences] = json.encodeToString(prefs)
            }
            it[lastActiveAt] = Instant.now()
        }

        getUserProfileById(userId)
    }

    suspend fun logoutUser(refreshToken: String?) = newSuspendedTransaction {
        refreshToken?.let {
            RefreshTokens.update({ RefreshTokens.token eq it }) {
                it[isRevoked] = true
            }
        }
    }

    suspend fun getUserStats(userId: String): Map<String, Any> = newSuspendedTransaction {
        val user = Users.selectAll()
            .where { Users.id eq UUID.fromString(userId) }
            .firstOrNull()
            ?: throw NotFoundException("User not found")

        mapOf(
            "totalScore" to user[Users.totalScore],
            "gamesPlayed" to user[Users.gamesPlayed],
            "gamesWon" to user[Users.gamesWon],
            "currentStreak" to user[Users.currentStreak],
            "bestStreak" to user[Users.bestStreak],
            "level" to user[Users.level],
            "winRate" to if (user[Users.gamesPlayed] > 0) {
                (user[Users.gamesWon].toDouble() / user[Users.gamesPlayed].toDouble()) * 100
            } else 0.0
        )
    }

    private fun createUserSession(userId: UUID, ipAddress: String?, userAgent: String?): String {
        val sessionToken = UUID.randomUUID().toString()

        UserSessions.insert {
            it[UserSessions.userId] = userId
            it[UserSessions.sessionToken] = sessionToken
            it[UserSessions.ipAddress] = ipAddress
            it[UserSessions.userAgent] = userAgent
            it[expiresAt] = Instant.now().plus(7, ChronoUnit.DAYS)
        }

        return sessionToken
    }

    private fun isUserOnline(userId: String): Boolean {
        // Check if user has any active sessions in the last 5 minutes
        val fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES)

        return UserSessions.selectAll()
            .where {
                (UserSessions.userId eq UUID.fromString(userId)) and
                        (UserSessions.isActive eq true) and
                        (UserSessions.createdAt greater fiveMinutesAgo)
            }
            .count() > 0
    }

    private fun calculateUserRank(level: Int, winRate: Double): String {
        return when {
            level < 5 -> "Beginner"
            level < 10 && winRate < 50 -> "Novice"
            level < 20 && winRate < 70 -> "Intermediate"
            level < 50 && winRate < 80 -> "Advanced"
            level < 100 && winRate < 90 -> "Expert"
            else -> "Master"
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"))
    }

    private fun isValidUsername(username: String): Boolean {
        return username.length in 3..50 && username.matches(Regex("^[a-zA-Z0-9_]+$"))
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            java.net.URL(url).toURI()
            true
        } catch (e: Exception) {
            false
        }
    }
}