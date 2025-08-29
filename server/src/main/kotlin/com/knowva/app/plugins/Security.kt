package com.knowva.app.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

fun Application.configureSecurity() {
    val jwtSecret = environment.config.propertyOrNull("jwt.secret")?.getString()
        ?: System.getenv("JWT_SECRET") ?: "your-default-secret-change-this"
    val jwtIssuer = environment.config.propertyOrNull("jwt.issuer")?.getString()
        ?: System.getenv("JWT_ISSUER") ?: "knowva-app"
    val jwtAudience = environment.config.propertyOrNull("jwt.audience")?.getString()
        ?: System.getenv("JWT_AUDIENCE") ?: "knowva-users"

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "Knowva API"
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(jwtIssuer)
                    .withAudience(jwtAudience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.subject != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}