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
fun RegisterScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Handle side effects
    HandleSideEffects(
        sideEffectFlow = viewModel.sideEffect,
        onSideEffect = { effect ->
            when (effect) {
                RegisterContract.SideEffect.NavigateToHome -> {
                    onNavigateToHome()
                }

                is RegisterContract.SideEffect.ShowError -> {
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

            // Register Card
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
                        title = "Join Knowva",
                        subtitle = "Create your account and start your trivia adventure"
                    )

                    Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))

                    // Email Field
                    AuthTextField(
                        value = state.email,
                        onValueChange = {
                            viewModel.handleIntent(
                                RegisterContract.Intent.EmailChanged(
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

                    // Username Field
                    AuthTextField(
                        value = state.username,
                        onValueChange = {
                            viewModel.handleIntent(
                                RegisterContract.Intent.UsernameChanged(
                                    it
                                )
                            )
                        },
                        label = "Username",
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        isError = state.usernameError != null,
                        errorMessage = state.usernameError
                    )

                    Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

                    // Display Name Field
                    AuthTextField(
                        value = state.displayName,
                        onValueChange = {
                            viewModel.handleIntent(
                                RegisterContract.Intent.DisplayNameChanged(
                                    it
                                )
                            )
                        },
                        label = "Display Name",
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        isError = state.displayNameError != null,
                        errorMessage = state.displayNameError
                    )

                    Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

                    // Password Field
                    AuthPasswordField(
                        value = state.password,
                        onValueChange = {
                            viewModel.handleIntent(
                                RegisterContract.Intent.PasswordChanged(
                                    it
                                )
                            )
                        },
                        label = "Password",
                        imeAction = ImeAction.Next,
                        isError = state.passwordError != null,
                        errorMessage = state.passwordError,
                        isPasswordVisible = state.isPasswordVisible,
                        onTogglePasswordVisibility = {
                            viewModel.handleIntent(RegisterContract.Intent.TogglePasswordVisibility)
                        }
                    )

                    Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

                    // Confirm Password Field
                    AuthPasswordField(
                        value = state.confirmPassword,
                        onValueChange = {
                            viewModel.handleIntent(
                                RegisterContract.Intent.ConfirmPasswordChanged(
                                    it
                                )
                            )
                        },
                        label = "Confirm Password",
                        imeAction = ImeAction.Done,
                        isError = state.confirmPasswordError != null,
                        errorMessage = state.confirmPasswordError,
                        isPasswordVisible = state.isConfirmPasswordVisible,
                        onTogglePasswordVisibility = {
                            viewModel.handleIntent(RegisterContract.Intent.ToggleConfirmPasswordVisibility)
                        },
                        onImeActionPerformed = {
                            keyboardController?.hide()
                            viewModel.handleIntent(RegisterContract.Intent.RegisterClicked)
                        }
                    )

                    if (state.registerError != null) {
                        Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))
                        AuthErrorMessage(message = state.registerError!!)
                    }

                    Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))

                    // Register Button
                    AuthPrimaryButton(
                        text = "Create Account",
                        onClick = { viewModel.handleIntent(RegisterContract.Intent.RegisterClicked) },
                        isLoading = state.isLoading,
                        enabled = state.email.isNotBlank() &&
                                state.username.isNotBlank() &&
                                state.displayName.isNotBlank() &&
                                state.password.isNotBlank() &&
                                state.confirmPassword.isNotBlank()
                    )

                    Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

                    // Terms Text
                    Text(
                        text = "By creating an account, you agree to our Terms of Service and Privacy Policy",
                        color = TriviaColors.OnSurfaceVariant,
                        style = TriviaTypography.LabelSmall,
                        modifier = Modifier.padding(horizontal = Dimensions.SpacingMedium)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))

            // Login Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = TriviaColors.OnSurfaceVariant,
                    style = TriviaTypography.BodyMedium
                )
                AuthTextButton(
                    text = "Sign In",
                    onClick = onNavigateToLogin
                )
            }
        }
    }
}