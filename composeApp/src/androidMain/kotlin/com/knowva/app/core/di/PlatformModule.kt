package com.knowva.app.core.di

import org.koin.dsl.module
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual val platformModule = module {

    // Android-specific HTTP client with OkHttp engine
    single {
        HttpClient(OkHttp) {
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

    // Android-specific storage and preferences
    // single { AndroidPreferencesManager(androidContext()) }
    // single { AndroidLocalStorage(androidContext()) }
}