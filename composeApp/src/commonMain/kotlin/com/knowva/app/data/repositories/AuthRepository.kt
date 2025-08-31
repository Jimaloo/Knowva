package com.knowva.app.data.repositories

import kotlinx.coroutines.flow.Flow
import com.knowva.app.data.remote.*
import com.knowva.app.data.local.AuthTokenStorage

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<UserProfileResponse>
    suspend fun register(
        email: String,
        password: String,
        username: String,
        displayName: String
    ): Result<UserProfileResponse>

    suspend fun logout(): Result<Unit>
    suspend fun getProfile(): Result<UserProfileResponse>
    suspend fun updateProfile(
        displayName: String?,
        avatarUrl: String?,
        preferences: UserPreferences?
    ): Result<UserProfileResponse>

    suspend fun isLoggedIn(): Boolean
    fun getAccessTokenFlow(): Flow<String?>
    suspend fun refreshTokenIfNeeded()
}

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val tokenStorage: AuthTokenStorage
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<UserProfileResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = authApiService.login(request)
            tokenStorage.saveTokens(response.accessToken, response.refreshToken)
            Result.success(response.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        username: String,
        displayName: String
    ): Result<UserProfileResponse> {
        return try {
            val request = RegisterRequest(email, password, username, displayName)
            val response = authApiService.register(request)
            tokenStorage.saveTokens(response.accessToken, response.refreshToken)
            Result.success(response.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val refreshToken = tokenStorage.getRefreshToken()
            if (refreshToken != null) {
                authApiService.logout(refreshToken)
            }
            tokenStorage.clearTokens()
            Result.success(Unit)
        } catch (e: Exception) {
            // Even if logout fails on server, clear local tokens
            tokenStorage.clearTokens()
            Result.failure(e)
        }
    }

    override suspend fun getProfile(): Result<UserProfileResponse> {
        return try {
            val profile = authApiService.getProfile()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(
        displayName: String?,
        avatarUrl: String?,
        preferences: UserPreferences?
    ): Result<UserProfileResponse> {
        return try {
            val request = UpdateProfileRequest(displayName, avatarUrl, preferences)
            val profile = authApiService.updateProfile(request)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenStorage.getAccessToken() != null
    }

    override fun getAccessTokenFlow(): Flow<String?> {
        return tokenStorage.getAccessTokenFlow()
    }

    override suspend fun refreshTokenIfNeeded() {
        // Implementation for token refresh logic if needed
        val refreshToken = tokenStorage.getRefreshToken()
        if (refreshToken != null) {
            try {
                val response = authApiService.refreshToken(refreshToken)
                tokenStorage.saveTokens(response.accessToken, response.refreshToken)
            } catch (e: Exception) {
                // If refresh fails, clear tokens
                tokenStorage.clearTokens()
            }
        }
    }
}