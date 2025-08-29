package com.knowva.app.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import com.knowva.app.models.*
import com.knowva.app.services.UserService
import com.knowva.app.exceptions.*

fun Route.authRoutes(userService: UserService) {
    route("/api/v1/auth") {
        post("/register") {
            try {
                val request = call.receive<RegisterRequest>()
                val ipAddress = call.request.local.remoteHost
                val userAgent = call.request.headers["User-Agent"]

                val response = userService.registerUser(request, ipAddress, userAgent)
                call.respond(HttpStatusCode.Created, response)
            } catch (e: ValidationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(e.message ?: "Validation failed", e.details)
                )
            } catch (e: ConflictException) {
                call.respond(HttpStatusCode.Conflict, ErrorResponse(e.message ?: "Conflict"))
            }
        }

        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                val ipAddress = call.request.local.remoteHost
                val userAgent = call.request.headers["User-Agent"]

                val response = userService.loginUser(request, ipAddress, userAgent)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: UnauthorizedException) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(e.message ?: "Authentication failed")
                )
            }
        }

        post("/refresh") {
            try {
                val request = call.receive<RefreshTokenRequest>()
                val response = userService.refreshToken(request)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: UnauthorizedException) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(e.message ?: "Invalid refresh token")
                )
            }
        }

        authenticate("auth-jwt") {
            post("/logout") {
                val refreshToken = call.request.headers["X-Refresh-Token"]
                userService.logoutUser(refreshToken)
                call.respond(HttpStatusCode.OK, SuccessResponse("Logged out successfully"))
            }

            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.subject
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse("Invalid token")
                    )

                try {
                    val userProfile = userService.getUserProfileById(userId)
                    call.respond(HttpStatusCode.OK, userProfile)
                } catch (e: NotFoundException) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(e.message ?: "User not found")
                    )
                }
            }

            put("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.subject
                    ?: return@put call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse("Invalid token")
                    )

                try {
                    val request = call.receive<UpdateProfileRequest>()
                    val userProfile = userService.updateUserProfile(userId, request)
                    call.respond(HttpStatusCode.OK, userProfile)
                } catch (e: NotFoundException) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(e.message ?: "User not found")
                    )
                } catch (e: ValidationException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(e.message ?: "Validation failed")
                    )
                }
            }

            get("/me/stats") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.subject
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse("Invalid token")
                    )

                try {
                    val stats = userService.getUserStats(userId)
                    call.respond(HttpStatusCode.OK, stats)
                } catch (e: NotFoundException) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(e.message ?: "User not found")
                    )
                }
            }
        }
    }
}