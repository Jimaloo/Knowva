package com.knowva.app.core.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.knowva.app.data.local.AuthTokenStorage
import io.ktor.client.request.header

expect fun createPlatformHttpClient(): HttpClient

fun createHttpClient(authTokenStorage: AuthTokenStorage): HttpClient {
    val client = createPlatformHttpClient()

    return client.config {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val accessToken = authTokenStorage.getAccessToken()
                    accessToken?.let {
                        BearerTokens(accessToken = it, refreshToken = "")
                    }
                }

                refreshTokens {
                    val refreshToken = authTokenStorage.getRefreshToken()
                    if (refreshToken != null) {
                        try {
                            // This will be handled by the repository refresh logic
                            val accessToken = authTokenStorage.getAccessToken()
                            accessToken?.let {
                                BearerTokens(accessToken = it, refreshToken = refreshToken)
                            }
                        } catch (e: Exception) {
                            null
                        }
                    } else {
                        null
                    }
                }
            }
        }

        install(DefaultRequest) {
            header("Content-Type", "application/json")
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 30000
        }
    }
}