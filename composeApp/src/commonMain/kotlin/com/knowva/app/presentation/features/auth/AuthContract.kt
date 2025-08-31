package com.knowva.app.presentation.features.auth

import com.knowva.app.data.remote.UserProfileResponse
import com.knowva.app.presentation.mvi.UiSideEffect
import com.knowva.app.presentation.mvi.UiIntent
import com.knowva.app.presentation.mvi.UiState

// Login Screen Contract
object LoginContract {
    data class State(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null,
        val loginError: String? = null,
        val isPasswordVisible: Boolean = false
    ) : UiState

    sealed class Intent : UiIntent {
        data class EmailChanged(val email: String) : Intent()
        data class PasswordChanged(val password: String) : Intent()
        object TogglePasswordVisibility : Intent()
        object LoginClicked : Intent()
        object ClearErrors : Intent()
    }

    sealed class SideEffect : UiSideEffect {
        object NavigateToHome : SideEffect()
        data class ShowError(val message: String) : SideEffect()
    }
}

// Register Screen Contract
object RegisterContract {
    data class State(
        val email: String = "",
        val username: String = "",
        val displayName: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val usernameError: String? = null,
        val displayNameError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null,
        val registerError: String? = null,
        val isPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false
    ) : UiState

    sealed class Intent : UiIntent {
        data class EmailChanged(val email: String) : Intent()
        data class UsernameChanged(val username: String) : Intent()
        data class DisplayNameChanged(val displayName: String) : Intent()
        data class PasswordChanged(val password: String) : Intent()
        data class ConfirmPasswordChanged(val confirmPassword: String) : Intent()
        object TogglePasswordVisibility : Intent()
        object ToggleConfirmPasswordVisibility : Intent()
        object RegisterClicked : Intent()
        object ClearErrors : Intent()
    }

    sealed class SideEffect : UiSideEffect {
        object NavigateToHome : SideEffect()
        data class ShowError(val message: String) : SideEffect()
    }
}

// Profile Screen Contract
object ProfileContract {
    data class State(
        val userProfile: UserProfileResponse? = null,
        val isLoading: Boolean = false,
        val isEditing: Boolean = false,
        val displayName: String = "",
        val avatarUrl: String? = null,
        val error: String? = null,
        val isUpdating: Boolean = false
    ) : UiState

    sealed class Intent : UiIntent {
        object LoadProfile : Intent()
        object StartEditing : Intent()
        object CancelEditing : Intent()
        data class DisplayNameChanged(val displayName: String) : Intent()
        data class AvatarUrlChanged(val avatarUrl: String) : Intent()
        object SaveProfile : Intent()
        object Logout : Intent()
        object ClearError : Intent()
    }

    sealed class SideEffect : UiSideEffect {
        object NavigateToAuth : SideEffect()
        data class ShowError(val message: String) : SideEffect()
        data class ShowSuccess(val message: String) : SideEffect()
    }
}