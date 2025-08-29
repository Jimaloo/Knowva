package com.knowva.app.presentation.features.home

import com.knowva.app.presentation.mvi.UiState
import com.knowva.app.presentation.mvi.UiIntent
import com.knowva.app.presentation.mvi.UiSideEffect
import com.knowva.app.domain.entities.*

/**
 * MVI Contract for Home Screen
 */

data class HomeState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val userLevel: UserLevel? = null,
    val dailyChallenge: DailyChallenge? = null,
    val weeklyStats: WeeklyStats? = null,
    val recentAchievements: List<Achievement> = emptyList(),
    val gameInvitations: List<GameInvitation> = emptyList(),
    val error: String? = null
) : UiState

sealed class HomeIntent : UiIntent {
    object LoadHomeData : HomeIntent()
    object RefreshHomeData : HomeIntent()
    object StartQuickMatch : HomeIntent()
    object OpenDailyChallenge : HomeIntent()
    object OpenProfile : HomeIntent()
    object OpenLeaderboards : HomeIntent()
    object OpenAchievements : HomeIntent()
    data class AcceptGameInvitation(val invitationId: String) : HomeIntent()
    data class DeclineGameInvitation(val invitationId: String) : HomeIntent()
    object DismissError : HomeIntent()
}

sealed class HomeSideEffect : UiSideEffect {
    object NavigateToQuickMatch : HomeSideEffect()
    object NavigateToDailyChallenge : HomeSideEffect()
    object NavigateToProfile : HomeSideEffect()
    object NavigateToLeaderboards : HomeSideEffect()
    object NavigateToAchievements : HomeSideEffect()
    data class NavigateToGame(val gameId: String) : HomeSideEffect()
    data class ShowLevelUpAnimation(val newLevel: Int) : HomeSideEffect()
    data class ShowAchievementUnlocked(val achievement: Achievement) : HomeSideEffect()
    data class ShowError(val message: String) : HomeSideEffect()
    data class ShowSuccess(val message: String) : HomeSideEffect()
}