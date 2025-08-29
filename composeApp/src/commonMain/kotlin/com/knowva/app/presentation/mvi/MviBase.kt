package com.knowva.app.presentation.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

/**
 * Base class for all MVI ViewModels
 * Provides consistent state management and side effect handling
 */
abstract class MviViewModel<State : UiState, Intent : UiIntent, SideEffect : UiSideEffect>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _sideEffect = Channel<SideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<SideEffect> = _sideEffect.receiveAsFlow()

    protected val currentState: State
        get() = _state.value

    /**
     * Handle incoming intents
     */
    abstract fun handleIntent(intent: Intent)

    /**
     * Update the current state
     */
    protected fun setState(reducer: State.() -> State) {
        _state.value = currentState.reducer()
    }

    /**
     * Emit a side effect
     */
    protected fun emitSideEffect(sideEffect: SideEffect) {
        viewModelScope.launch {
            _sideEffect.send(sideEffect)
        }
    }
}

/**
 * Base interface for UI states
 */
interface UiState

/**
 * Base interface for UI intents
 */
interface UiIntent

/**
 * Base interface for side effects
 */
interface UiSideEffect

/**
 * Composable function to handle side effects
 */
@Composable
fun <SideEffect : UiSideEffect> HandleSideEffects(
    sideEffectFlow: Flow<SideEffect>,
    onSideEffect: (SideEffect) -> Unit
) {
    val sideEffect by sideEffectFlow.collectAsState(initial = null)

    LaunchedEffect(sideEffect) {
        sideEffect?.let(onSideEffect)
    }
}

/**
 * Loading state wrapper
 */
sealed class LoadingState<out T> {
    object Idle : LoadingState<Nothing>()
    object Loading : LoadingState<Nothing>()
    data class Success<T>(val data: T) : LoadingState<T>()
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "Unknown error"
    ) : LoadingState<Nothing>()

    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isIdle: Boolean get() = this is Idle
}

/**
 * Extension function to get data from LoadingState
 */
fun <T> LoadingState<T>.getDataOrNull(): T? = when (this) {
    is LoadingState.Success -> data
    else -> null
}

/**
 * Extension function to get error from LoadingState
 */
fun <T> LoadingState<T>.getErrorOrNull(): String? = when (this) {
    is LoadingState.Error -> message
    else -> null
}