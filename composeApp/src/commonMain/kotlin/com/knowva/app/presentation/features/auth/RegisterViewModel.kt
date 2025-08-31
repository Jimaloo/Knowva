package com.knowva.app.presentation.features.auth

import androidx.lifecycle.viewModelScope
import com.knowva.app.data.repositories.AuthRepository
import com.knowva.app.presentation.mvi.MviViewModel
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : MviViewModel<RegisterContract.State, RegisterContract.Intent, RegisterContract.SideEffect>(
    initialState = RegisterContract.State()
) {

    override fun handleIntent(intent: RegisterContract.Intent) {
        when (intent) {
            is RegisterContract.Intent.EmailChanged -> {
                setState {
                    copy(
                        email = intent.email,
                        emailError = null,
                        registerError = null
                    )
                }
            }

            is RegisterContract.Intent.UsernameChanged -> {
                setState {
                    copy(
                        username = intent.username,
                        usernameError = null,
                        registerError = null
                    )
                }
            }

            is RegisterContract.Intent.DisplayNameChanged -> {
                setState {
                    copy(
                        displayName = intent.displayName,
                        displayNameError = null,
                        registerError = null
                    )
                }
            }

            is RegisterContract.Intent.PasswordChanged -> {
                setState {
                    copy(
                        password = intent.password,
                        passwordError = null,
                        confirmPasswordError = if (confirmPassword.isNotEmpty() && confirmPassword != intent.password) {
                            "Passwords don't match"
                        } else null,
                        registerError = null
                    )
                }
            }

            is RegisterContract.Intent.ConfirmPasswordChanged -> {
                setState {
                    copy(
                        confirmPassword = intent.confirmPassword,
                        confirmPasswordError = if (password != intent.confirmPassword) {
                            "Passwords don't match"
                        } else null,
                        registerError = null
                    )
                }
            }

            RegisterContract.Intent.TogglePasswordVisibility -> {
                setState {
                    copy(isPasswordVisible = !isPasswordVisible)
                }
            }

            RegisterContract.Intent.ToggleConfirmPasswordVisibility -> {
                setState {
                    copy(isConfirmPasswordVisible = !isConfirmPasswordVisible)
                }
            }

            RegisterContract.Intent.RegisterClicked -> {
                performRegistration()
            }

            RegisterContract.Intent.ClearErrors -> {
                setState {
                    copy(
                        emailError = null,
                        usernameError = null,
                        displayNameError = null,
                        passwordError = null,
                        confirmPasswordError = null,
                        registerError = null
                    )
                }
            }
        }
    }

    private fun performRegistration() {
        val currentState = state.value

        // Validate input
        val emailError = validateEmail(currentState.email)
        val usernameError = validateUsername(currentState.username)
        val displayNameError = validateDisplayName(currentState.displayName)
        val passwordError = validatePassword(currentState.password)
        val confirmPasswordError =
            validateConfirmPassword(currentState.password, currentState.confirmPassword)

        if (emailError != null || usernameError != null || displayNameError != null ||
            passwordError != null || confirmPasswordError != null
        ) {
            setState {
                copy(
                    emailError = emailError,
                    usernameError = usernameError,
                    displayNameError = displayNameError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        setState { copy(isLoading = true) }

        viewModelScope.launch {
            authRepository.register(
                email = currentState.email,
                password = currentState.password,
                username = currentState.username,
                displayName = currentState.displayName
            ).onSuccess {
                setState { copy(isLoading = false) }
                emitSideEffect(RegisterContract.SideEffect.NavigateToHome)
            }.onFailure { throwable ->
                setState {
                    copy(
                        isLoading = false,
                        registerError = throwable.message ?: "Registration failed"
                    )
                }
                emitSideEffect(
                    RegisterContract.SideEffect.ShowError(
                        throwable.message ?: "Registration failed"
                    )
                )
            }
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !email.contains("@") -> "Invalid email format"
            else -> null
        }
    }

    private fun validateUsername(username: String): String? {
        return when {
            username.isBlank() -> "Username is required"
            username.length < 3 -> "Username must be at least 3 characters"
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> "Username can only contain letters, numbers, and underscores"
            else -> null
        }
    }

    private fun validateDisplayName(displayName: String): String? {
        return when {
            displayName.isBlank() -> "Display name is required"
            displayName.length < 2 -> "Display name must be at least 2 characters"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Confirm password is required"
            password != confirmPassword -> "Passwords don't match"
            else -> null
        }
    }
}