package com.knowva.app.presentation.features.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.knowva.app.presentation.mvi.MviViewModel
import com.knowva.app.domain.entities.*

/**
 * Home Screen ViewModel with MVI pattern
 */
class HomeViewModel(
    // TODO: Add use cases when implemented
    // private val getUserProfileUseCase: GetUserProfileUseCase,
    // private val getDailyChallengeUseCase: GetDailyChallengeUseCase
) : MviViewModel<HomeState, HomeIntent, HomeSideEffect>(
    initialState = HomeState()
) {

    init {
        handleIntent(HomeIntent.LoadHomeData)
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadHomeData -> loadHomeData()
            HomeIntent.RefreshHomeData -> refreshHomeData()
            HomeIntent.StartQuickMatch -> startQuickMatch()
            HomeIntent.OpenDailyChallenge -> openDailyChallenge()
            HomeIntent.OpenProfile -> openProfile()
            HomeIntent.OpenLeaderboards -> openLeaderboards()
            HomeIntent.OpenAchievements -> openAchievements()
            is HomeIntent.AcceptGameInvitation -> acceptGameInvitation(intent.invitationId)
            is HomeIntent.DeclineGameInvitation -> declineGameInvitation(intent.invitationId)
            HomeIntent.DismissError -> dismissError()
        }
    }

    private fun loadHomeData() {
        setState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // TODO: Replace with actual API calls
                // Simulate loading with mock data for now
                val mockUser = createMockUser()
                val mockUserLevel = createMockUserLevel()
                val mockDailyChallenge = createMockDailyChallenge()
                val mockWeeklyStats = createMockWeeklyStats()
                val mockAchievements = createMockAchievements()

                setState {
                    copy(
                        isLoading = false,
                        user = mockUser,
                        userLevel = mockUserLevel,
                        dailyChallenge = mockDailyChallenge,
                        weeklyStats = mockWeeklyStats,
                        recentAchievements = mockAchievements,
                        gameInvitations = emptyList()
                    )
                }
            } catch (e: Exception) {
                setState {
                    copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load data"
                    )
                }
            }
        }
    }

    private fun refreshHomeData() {
        loadHomeData()
    }

    private fun startQuickMatch() {
        emitSideEffect(HomeSideEffect.NavigateToQuickMatch)
    }

    private fun openDailyChallenge() {
        emitSideEffect(HomeSideEffect.NavigateToDailyChallenge)
    }

    private fun openProfile() {
        emitSideEffect(HomeSideEffect.NavigateToProfile)
    }

    private fun openLeaderboards() {
        emitSideEffect(HomeSideEffect.NavigateToLeaderboards)
    }

    private fun openAchievements() {
        emitSideEffect(HomeSideEffect.NavigateToAchievements)
    }

    private fun acceptGameInvitation(invitationId: String) {
        // TODO: Implement accept invitation logic
        emitSideEffect(HomeSideEffect.NavigateToGame(invitationId))
    }

    private fun declineGameInvitation(invitationId: String) {
        // TODO: Implement decline invitation logic
        val updatedInvitations = currentState.gameInvitations.filter { it.id != invitationId }
        setState { copy(gameInvitations = updatedInvitations) }
    }

    private fun dismissError() {
        setState { copy(error = null) }
    }

    // Mock data creation functions - TODO: Replace with actual data layer
    private fun createMockUser(): User {
        return User(
            id = "user123",
            username = "player123",
            displayName = "John Doe",
            email = "john@example.com",
            avatarUrl = null,
            level = 15,
            totalXP = 12500,
            currentXP = 800,
            xpToNextLevel = 1200,
            totalScore = 89500,
            gamesPlayed = 120,
            gamesWon = 85,
            currentStreak = 7,
            bestStreak = 25,
            rank = UserRank.INTERMEDIATE,
            badges = listOf("First Win", "Streak Master", "Science Expert"),
            createdAt = "2024-01-15T10:00:00Z",
            lastActiveAt = "2024-01-20T15:30:00Z",
            isOnline = true,
            // Enhanced gamification
            coins = 2_450L,
            gems = 127L,
            powerUps = mapOf(
                PowerUpType.FIFTY_FIFTY to 3,
                PowerUpType.EXTRA_TIME to 2,
                PowerUpType.HINT to 5,
                PowerUpType.DOUBLE_XP to 1,
                PowerUpType.SKIP_QUESTION to 2
            ),
            questProgress = createMockQuests(),
            socialStats = SocialStats(
                friendsCount = 23,
                challengesSent = 45,
                challengesWon = 32,
                helpGiven = 18,
                helpReceived = 12
            ),
            seasonalRank = SeasonalRank(
                tier = RankTier.GOLD,
                division = 3,
                points = 1847,
                seasonId = "season_2024_1",
                peakTier = RankTier.GOLD,
                peakDivision = 2
            )
        )
    }

    private fun createMockQuests(): List<Quest> {
        return listOf(
            Quest(
                id = "quest_daily_1",
                title = "Daily Streaker",
                description = "Get a 5-question streak in any game mode",
                type = QuestType.DAILY,
                requirements = QuestRequirements(streakAchieved = 5),
                rewards = listOf(
                    Reward(RewardType.XP, 250, "250 XP bonus", RewardRarity.COMMON),
                    Reward(RewardType.COINS, 100, "100 coins", RewardRarity.COMMON)
                ),
                progress = 3,
                maxProgress = 5,
                difficulty = DifficultyLevel.EASY,
                expiresAt = "2024-01-21T23:59:59Z"
            ),
            Quest(
                id = "quest_weekly_1",
                title = "Science Master",
                description = "Answer 25 science questions correctly this week",
                type = QuestType.WEEKLY,
                requirements = QuestRequirements(
                    questionsAnswered = 25,
                    categoriesPlayed = listOf(GameCategory.SCIENCE)
                ),
                rewards = listOf(
                    Reward(RewardType.XP, 500, "500 XP bonus", RewardRarity.UNCOMMON),
                    Reward(RewardType.GEMS, 25, "25 gems", RewardRarity.RARE),
                    Reward(RewardType.POWER_UP, 2, "2 Hint power-ups", RewardRarity.UNCOMMON)
                ),
                progress = 18,
                maxProgress = 25,
                difficulty = DifficultyLevel.MEDIUM,
                expiresAt = "2024-01-28T23:59:59Z"
            ),
            Quest(
                id = "quest_achievement_1",
                title = "Social Butterfly",
                description = "Challenge 3 friends to games",
                type = QuestType.SOCIAL,
                requirements = QuestRequirements(socialAction = SocialActionType.CHALLENGE_FRIEND),
                rewards = listOf(
                    Reward(RewardType.XP, 300, "300 XP bonus", RewardRarity.COMMON),
                    Reward(RewardType.COINS, 150, "150 coins", RewardRarity.COMMON),
                    Reward(RewardType.BADGE, 1, "Social Butterfly badge", RewardRarity.RARE)
                ),
                progress = 1,
                maxProgress = 3,
                difficulty = DifficultyLevel.EASY,
                isCompleted = false
            )
        )
    }

    private fun createMockUserLevel(): UserLevel {
        return UserLevel(
            currentLevel = 15,
            levelName = "Knowledge Seeker",
            currentXP = 800,
            xpToNextLevel = 1200,
            progressPercentage = 66.7f,
            levelColor = "#FF9800",
            unlockedFeatures = listOf("Daily Challenges", "Power-ups", "Custom Avatars")
        )
    }

    private fun createMockDailyChallenge(): DailyChallenge {
        return DailyChallenge(
            id = "daily_001",
            title = "Science Master",
            description = "Answer 10 science questions correctly",
            category = GameCategory.SCIENCE,
            difficulty = DifficultyLevel.MEDIUM,
            requirements = ChallengeRequirements(
                correctAnswers = 10,
                category = GameCategory.SCIENCE
            ),
            reward = ChallengeReward(RewardType.XP, 500, "500 XP"),
            progress = 6,
            maxProgress = 10,
            isCompleted = false,
            expiresAt = "2024-01-21T23:59:59Z"
        )
    }

    private fun createMockWeeklyStats(): WeeklyStats {
        return WeeklyStats(
            rank = 42,
            totalPlayers = 1250,
            gamesPlayed = 18,
            xpEarned = 2400,
            streak = 7,
            weekStartDate = "2024-01-15T00:00:00Z"
        )
    }

    private fun createMockAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = "achievement_001",
                name = "First Steps",
                description = "Complete your first game",
                iconUrl = "https://example.com/first_steps.png",
                rarity = AchievementRarity.COMMON,
                category = AchievementCategory.GAMEPLAY,
                isUnlocked = true,
                unlockedAt = "2024-01-15T12:00:00Z",
                xpReward = 100,
                coinReward = 50
            ),
            Achievement(
                id = "achievement_002",
                name = "Streak Starter",
                description = "Get a 5-question streak",
                iconUrl = "https://example.com/streak_starter.png",
                rarity = AchievementRarity.UNCOMMON,
                category = AchievementCategory.GAMEPLAY,
                isUnlocked = true,
                unlockedAt = "2024-01-16T14:30:00Z",
                xpReward = 250,
                coinReward = 100
            )
        )
    }
}