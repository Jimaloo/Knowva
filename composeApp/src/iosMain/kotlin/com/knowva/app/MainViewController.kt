package com.knowva.app

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.startKoin
import com.knowva.app.core.di.appModule
import com.knowva.app.core.di.platformModule

fun MainViewController() = ComposeUIViewController {
    try {
        startKoin {
            modules(appModule, platformModule)
        }
    } catch (e: Exception) {
        // Koin already started, ignore
    }
    App()
}