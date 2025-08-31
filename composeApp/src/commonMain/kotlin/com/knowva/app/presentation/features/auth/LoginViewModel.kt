package com.knowva.app.presentation.features.auth

import androidx.lifecycle.viewModelScope
import com.knowva.app.data.repositories.AuthRepository
import com.knowva.app.presentation.mvi.MviViewModel
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : MviViewModel<LoginContract.State, LoginContract.Intent, LoginContract.SideEffect>(
    initialState = LoginContract.State()
) {

    override fun handleIntent(intent: LoginContract.Intent) {
        when (intent) {
            is LoginContract.Intent.EmailChanged -> {
                setState {
                    copy(
                        email = intent.email,
                        emailError = null,
                        loginError = null
                    )
                }
            }

            is LoginContract.Intent.PasswordChanged -> {
                setState {
                    copy(
                        password = intent.password,
                        passwordError = null,
                        loginError = null
                    )
                }
            }

            LoginContract.Intent.TogglePasswordVisibility -> {
                setState {
                    copy(isPasswordVisible = !isPasswordVisible)
                }
            }

            LoginContract.Intent.LoginClicked -> {
                performLogin()
            }

            LoginContract.Intent.ClearErrors -> {
                setState {
                    copy(
                        emailError = null,
                        passwordError = null,
                        loginError = null
                    )
                }
            }
        }
    }

    private fun performLogin() {
        val currentState = state.value

        // Validate input
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)

        if (emailError != null || passwordError != null) {
            setState {
                copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        setState { copy(isLoading = true) }

        viewModelScope.launch {
            authRepository.login(currentState.email, currentState.password)
                .onSuccess {
                    setState { copy(isLoading = false) }
                    emitSideEffect(LoginContract.SideEffect.NavigateToHome)
                }
                .onFailure { throwable ->
                    setState {
                        copy(
                            isLoading = false,
                            loginError = throwable.message ?: "Login failed"
                        )
                    }
                    emitSideEffect(
                        LoginContract.SideEffect.ShowError(
                            throwable.message ?: "Login failed"
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

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }
}