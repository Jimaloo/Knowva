package com.knowva.app.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

interface AuthApiService {
    suspend fun register(request: RegisterRequest): AuthResponse
    suspend fun login(request: LoginRequest): AuthResponse
    suspend fun refreshToken(token: String): AuthResponse
    suspend fun logout(refreshToken: String)
    suspend fun getProfile(): UserProfileResponse
    suspend fun updateProfile(request: UpdateProfileRequest): UserProfileResponse
}

class AuthApiServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "http://localhost:8080"
) : AuthApiService {

    override suspend fun register(request: RegisterRequest): AuthResponse {
        return httpClient.post("$baseUrl/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun login(request: LoginRequest): AuthResponse {
        return httpClient.post("$baseUrl/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun refreshToken(token: String): AuthResponse {
        return httpClient.post("$baseUrl/api/v1/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequest(token))
        }.body()
    }

    override suspend fun logout(refreshToken: String) {
        httpClient.post("$baseUrl/api/v1/auth/logout") {
            header("X-Refresh-Token", refreshToken)
        }
    }

    override suspend fun getProfile(): UserProfileResponse {
        return httpClient.get("$baseUrl/api/v1/auth/me").body()
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): UserProfileResponse {
        return httpClient.put("$baseUrl/api/v1/auth/me") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}

// Data models matching server implementation
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String,
    val displayName: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class UpdateProfileRequest(
    val displayName: String? = null,
    val avatarUrl: String? = null,
    val preferences: UserPreferences? = null
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserProfileResponse,
    val expiresIn: Long
)

@Serializable
data class UserProfileResponse(
    val id: String,
    val username: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String? = null,
    val level: Int = 1,
    val totalScore: Long = 0,
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val winRate: Double = 0.0,
    val rank: String = "Beginner",
    val badges: List<String> = emptyList(),
    val preferences: UserPreferences,
    val createdAt: String,
    val lastActiveAt: String,
    val isOnline: Boolean = false
)

@Serializable
data class UserPreferences(
    val preferredCategories: List<String> = emptyList(),
    val difficultyLevel: String = "Mixed",
    val soundEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val profileVisibility: String = "Public"
)

@Serializable
data class ErrorResponse(
    val error: String,
    val details: String? = null,
    val timestamp: String
)