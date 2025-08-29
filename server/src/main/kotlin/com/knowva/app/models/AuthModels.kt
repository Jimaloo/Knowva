package com.knowva.app.models

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


// Authentication Request Models
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

// Authentication Response Models
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
    val profileVisibility: String = "Public" // Public, Friends, Private
)

// Response Models
@Serializable
data class ErrorResponse @OptIn(ExperimentalTime::class) constructor(
    val error: String,
    val details: String? = null,
    val timestamp: String = Clock.System.now().toString()
)

@Serializable
data class SuccessResponse @OptIn(ExperimentalTime::class) constructor(
    val message: String,
    val timestamp: String = Clock.System.now().toString()
)