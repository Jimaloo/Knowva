package com.knowva.app.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

// JWT Configuration
data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val accessTokenExpiration: Long = 30 * 60 * 1000, // 30 minutes
    val refreshTokenExpiration: Long = 30 * 24 * 60 * 60 * 1000 // 30 days
)

// JWT Service
class JwtService(private val config: JwtConfig) {
    private val algorithm = Algorithm.HMAC256(config.secret)

    fun generateAccessToken(userId: String, sessionId: String): String {
        return JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withSubject(userId)
            .withClaim("sessionId", sessionId)
            .withClaim("type", "access")
            .withExpiresAt(Date(System.currentTimeMillis() + config.accessTokenExpiration))
            .withIssuedAt(Date())
            .sign(algorithm)
    }

    fun generateRefreshToken(): String {
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString()
    }

    fun validateToken(token: String): DecodedJWT? {
        return try {
            val verifier = JWT.require(algorithm)
                .withIssuer(config.issuer)
                .withAudience(config.audience)
                .build()
            verifier.verify(token)
        } catch (e: Exception) {
            null
        }
    }

    fun extractUserId(token: String): String? {
        return validateToken(token)?.subject
    }
}