package com.knowva.app.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

// Database Tables
object Users : Table() {
    val id = uuid("id").autoGenerate()
    val username = varchar("username", 50).uniqueIndex()
    val displayName = varchar("display_name", 100)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val avatarUrl = varchar("avatar_url", 500).nullable()

    // Game Statistics
    val level = integer("level").default(1)
    val totalScore = long("total_score").default(0)
    val gamesPlayed = integer("games_played").default(0)
    val gamesWon = integer("games_won").default(0)
    val currentStreak = integer("current_streak").default(0)
    val bestStreak = integer("best_streak").default(0)

    // User Preferences (JSON stored as text)
    val preferences = text("preferences").default("{}")
    val badges = text("badges").default("[]")

    // Account Status
    val isVerified = bool("is_verified").default(false)
    val isActive = bool("is_active").default(true)
    val isPremium = bool("is_premium").default(false)

    // Timestamps
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val lastActiveAt = timestamp("last_active_at").defaultExpression(CurrentTimestamp)
    val emailVerifiedAt = timestamp("email_verified_at").nullable()

    override val primaryKey = PrimaryKey(id)
}

object RefreshTokens : Table() {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val token = varchar("token", 255).uniqueIndex()
    val deviceId = varchar("device_id", 255).nullable()
    val deviceName = varchar("device_name", 255).nullable()
    val expiresAt = timestamp("expires_at")
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val lastUsedAt = timestamp("last_used_at").defaultExpression(CurrentTimestamp)
    val isRevoked = bool("is_revoked").default(false)

    override val primaryKey = PrimaryKey(id)
}

object UserSessions : Table() {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val sessionToken = varchar("session_token", 255).uniqueIndex()
    val ipAddress = varchar("ip_address", 45).nullable()
    val userAgent = text("user_agent").nullable()
    val isActive = bool("is_active").default(true)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val expiresAt = timestamp("expires_at")

    override val primaryKey = PrimaryKey(id)
}