package com.knowva.app.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import com.knowva.app.services.*
import com.knowva.app.routes.authRoutes

fun Application.configureRouting() {
    // Initialize services
    val jwtConfig = JwtConfig(
        secret = environment.config.propertyOrNull("jwt.secret")?.getString()
            ?: System.getenv("JWT_SECRET") ?: "your-default-secret-change-this",
        issuer = environment.config.propertyOrNull("jwt.issuer")?.getString()
            ?: System.getenv("JWT_ISSUER") ?: "knowva-app",
        audience = environment.config.propertyOrNull("jwt.audience")?.getString()
            ?: System.getenv("JWT_AUDIENCE") ?: "knowva-users"
    )

    val jwtService = JwtService(jwtConfig)
    val passwordService = PasswordService()
    val userService = UserService(jwtService, passwordService)

    routing {
        // Health check endpoints
        get("/") {
            call.respond(
                HttpStatusCode.OK, mapOf(
                    "service" to "Knowva API",
                    "version" to "1.0.0",
                    "status" to "healthy",
                    "endpoints" to mapOf(
                        "auth" to "/api/v1/auth",
                        "health" to "/health"
                    )
                )
            )
        }

        get("/health") {
            call.respond(
                HttpStatusCode.OK, mapOf(
                    "status" to "healthy",
                    "timestamp" to System.currentTimeMillis(),
                    "database" to "connected"
                )
            )
        }

        // Authentication routes
        authRoutes(userService)

        // API documentation
        get("/api/v1/docs") {
            call.respond(
                HttpStatusCode.OK, mapOf(
                    "title" to "Knowva API",
                    "version" to "1.0.0",
                    "description" to "Authentication API for Knowva trivia game",
                    "endpoints" to mapOf(
                        "POST /api/v1/auth/register" to "Register new user account",
                        "POST /api/v1/auth/login" to "Login with email and password",
                        "POST /api/v1/auth/refresh" to "Refresh access token",
                        "POST /api/v1/auth/logout" to "Logout and invalidate tokens (requires auth)",
                        "GET /api/v1/auth/me" to "Get current user profile (requires auth)",
                        "PUT /api/v1/auth/me" to "Update user profile (requires auth)",
                        "GET /api/v1/auth/me/stats" to "Get user statistics (requires auth)"
                    )
                )
            )
        }
    }
}