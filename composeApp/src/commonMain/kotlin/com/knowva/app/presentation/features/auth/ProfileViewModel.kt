package com.knowva.app.presentation.features.auth

import androidx.lifecycle.viewModelScope
import com.knowva.app.data.repositories.AuthRepository
import com.knowva.app.data.remote.UserPreferences
import com.knowva.app.presentation.mvi.MviViewModel
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository
) : MviViewModel<ProfileContract.State, ProfileContract.Intent, ProfileContract.SideEffect>(
    initialState = ProfileContract.State()
) {

    init {
        handleIntent(ProfileContract.Intent.LoadProfile)
    }

    override fun handleIntent(intent: ProfileContract.Intent) {
        when (intent) {
            ProfileContract.Intent.LoadProfile -> {
                loadProfile()
            }

            ProfileContract.Intent.StartEditing -> {
                val currentProfile = state.value.userProfile
                setState {
                    copy(
                        isEditing = true,
                        displayName = currentProfile?.displayName ?: "",
                        avatarUrl = currentProfile?.avatarUrl
                    )
                }
            }

            ProfileContract.Intent.CancelEditing -> {
                setState {
                    copy(
                        isEditing = false,
                        displayName = "",
                        avatarUrl = null,
                        error = null
                    )
                }
            }

            is ProfileContract.Intent.DisplayNameChanged -> {
                setState {
                    copy(
                        displayName = intent.displayName,
                        error = null
                    )
                }
            }

            is ProfileContract.Intent.AvatarUrlChanged -> {
                setState {
                    copy(
                        avatarUrl = intent.avatarUrl,
                        error = null
                    )
                }
            }

            ProfileContract.Intent.SaveProfile -> {
                saveProfile()
            }

            ProfileContract.Intent.Logout -> {
                performLogout()
            }

            ProfileContract.Intent.ClearError -> {
                setState {
                    copy(error = null)
                }
            }
        }
    }

    private fun loadProfile() {
        setState { copy(isLoading = true) }

        viewModelScope.launch {
            authRepository.getProfile()
                .onSuccess { profile ->
                    setState {
                        copy(
                            userProfile = profile,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { throwable ->
                    setState {
                        copy(
                            isLoading = false,
                            error = throwable.message ?: "Failed to load profile"
                        )
                    }
                    emitSideEffect(
                        ProfileContract.SideEffect.ShowError(
                            throwable.message ?: "Failed to load profile"
                        )
                    )
                }
        }
    }

    private fun saveProfile() {
        val currentState = state.value

        if (currentState.displayName.isBlank()) {
            setState {
                copy(error = "Display name cannot be empty")
            }
            return
        }

        setState { copy(isUpdating = true) }

        viewModelScope.launch {
            authRepository.updateProfile(
                displayName = currentState.displayName,
                avatarUrl = currentState.avatarUrl,
                preferences = null // Keep existing preferences for now
            ).onSuccess { updatedProfile ->
                setState {
                    copy(
                        userProfile = updatedProfile,
                        isUpdating = false,
                        isEditing = false,
                        error = null
                    )
                }
                emitSideEffect(
                    ProfileContract.SideEffect.ShowSuccess("Profile updated successfully")
                )
            }.onFailure { throwable ->
                setState {
                    copy(
                        isUpdating = false,
                        error = throwable.message ?: "Failed to update profile"
                    )
                }
                emitSideEffect(
                    ProfileContract.SideEffect.ShowError(
                        throwable.message ?: "Failed to update profile"
                    )
                )
            }
        }
    }

    private fun performLogout() {
        viewModelScope.launch {
            authRepository.logout()
                .onSuccess {
                    emitSideEffect(ProfileContract.SideEffect.NavigateToAuth)
                }
                .onFailure { throwable ->
                    // Even if logout fails on server, navigate to auth
                    emitSideEffect(ProfileContract.SideEffect.NavigateToAuth)
                }
        }
    }
}