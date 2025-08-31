package com.knowva.app.presentation.features.auth

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun AuthMainScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoginMode by remember { mutableStateOf(true) }

    if (isLoginMode) {
        LoginScreen(
            onNavigateToHome = onNavigateToHome,
            onNavigateToRegister = { isLoginMode = false },
            modifier = modifier
        )
    } else {
        RegisterScreen(
            onNavigateToHome = onNavigateToHome,
            onNavigateToLogin = { isLoginMode = true },
            modifier = modifier
        )
    }
}