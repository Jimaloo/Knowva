package com.knowva.app.presentation.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.knowva.app.presentation.mvi.HandleSideEffects
import com.knowva.app.presentation.ui.components.*
import com.knowva.app.presentation.ui.theme.*
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Handle side effects
    HandleSideEffects(
        sideEffectFlow = viewModel.sideEffect,
        onSideEffect = { effect ->
            when (effect) {
                LoginContract.SideEffect.NavigateToHome -> {
                    onNavigateToHome()
                }

                is LoginContract.SideEffect.ShowError -> {
                    // Error is already shown in the UI via state
                }
            }
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        TriviaColors.GradientStart,
                        TriviaColors.GradientEnd
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimensions.SpacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo/Title
            Text(
                text = "KNOWVA",
                style = TriviaTypography.HeadlineLarge,
                color = TriviaColors.OnSurface,
                modifier = Modifier.padding(bottom = Dimensions.SpacingMedium)
            )

            // Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimensions.SpacingMedium),
                colors = CardDefaults.cardColors(
                    containerColor = TriviaColors.Surface
                ),
                shape = TriviaShapes.Large
            ) {
                Column(
                    modifier = Modifier.padding(Dimensions.SpacingLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuthHeader(
                        title = "Welcome Back",
                        subtitle = "Sign in to continue your trivia journey"
                    )

                    Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))

                    // Email Field
                    AuthTextField(
                        value = state.email,
                        onValueChange = {
                            viewModel.handleIntent(
                                LoginContract.Intent.EmailChanged(
                                    it
                                )
                            )
                        },
                        label = "Email",
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                        isError = state.emailError != null,
                        errorMessage = state.emailError
                    )

                    Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

                    // Password Field
                    AuthPasswordField(
                        value = state.password,
                        onValueChange = {
                            viewModel.handleIntent(
                                LoginContract.Intent.PasswordChanged(
                                    it
                                )
                            )
                        },
                        label = "Password",
                        imeAction = ImeAction.Done,
                        isError = state.passwordError != null,
                        errorMessage = state.passwordError,
                        isPasswordVisible = state.isPasswordVisible,
                        onTogglePasswordVisibility = {
                            viewModel.handleIntent(LoginContract.Intent.TogglePasswordVisibility)
                        },
                        onImeActionPerformed = {
                            keyboardController?.hide()
                            viewModel.handleIntent(LoginContract.Intent.LoginClicked)
                        }
                    )

                    if (state.loginError != null) {
                        Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))
                        AuthErrorMessage(message = state.loginError!!)
                    }

                    Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))

                    // Login Button
                    AuthPrimaryButton(
                        text = "Sign In",
                        onClick = { viewModel.handleIntent(LoginContract.Intent.LoginClicked) },
                        isLoading = state.isLoading,
                        enabled = state.email.isNotBlank() && state.password.isNotBlank()
                    )

                    Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

                    // Forgot Password
                    AuthTextButton(
                        text = "Forgot Password?",
                        onClick = { /* TODO: Implement forgot password */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))

            // Register Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = TriviaColors.OnSurfaceVariant,
                    style = TriviaTypography.BodyMedium
                )
                AuthTextButton(
                    text = "Sign Up",
                    onClick = onNavigateToRegister
                )
            }
        }
    }
}