package com.knowva.app.core.di

import org.koin.dsl.module
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual val platformModule = module {

    // iOS-specific HTTP client with Darwin engine
    single {
        HttpClient(Darwin) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                })
            }

            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }

    // iOS-specific storage and preferences
    // single { IOSPreferencesManager() }
    // single { IOSLocalStorage() }
}