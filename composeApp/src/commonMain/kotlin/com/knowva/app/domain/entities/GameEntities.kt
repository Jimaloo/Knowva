package com.knowva.app.domain.entities

import androidx.compose.ui.graphics.Color

/**
 * Core domain entities for the gamified trivia app
 */

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String? = null,
    val level: Int = 1,
    val totalXP: Long = 0,
    val currentXP: Long = 0,
    val xpToNextLevel: Long = 1000,
    val totalScore: Long = 0,
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val rank: UserRank = UserRank.BEGINNER,
    val badges: List<String> = emptyList(),
    val preferences: UserPreferences = UserPreferences(),
    val createdAt: String,
    val lastActiveAt: String,
    val isOnline: Boolean = false,
    val isPremium: Boolean = false,
    // Enhanced gamification
    val coins: Long = 0,
    val gems: Long = 0,
    val powerUps: Map<PowerUpType, Int> = emptyMap(),
    val questProgress: List<Quest> = emptyList(),
    val socialStats: SocialStats = SocialStats(),
    val seasonalRank: SeasonalRank = SeasonalRank()
) {
    val winRate: Double
        get() = if (gamesPlayed > 0) (gamesWon.toDouble() / gamesPlayed.toDouble()) * 100 else 0.0

    val progressPercentage: Float
        get() = if (xpToNextLevel > 0) (currentXP.toFloat() / xpToNextLevel.toFloat()) else 0f

    val totalWorth: Long
        get() = coins + (gems * 10) // Gems are worth 10 coins each
}

data class SocialStats(
    val friendsCount: Int = 0,
    val challengesSent: Int = 0,
    val challengesWon: Int = 0,
    val helpGiven: Int = 0,
    val helpReceived: Int = 0,
    val guild: Guild? = null
)

data class Guild(
    val id: String,
    val name: String,
    val description: String,
    val memberCount: Int,
    val level: Int,
    val xp: Long,
    val iconUrl: String?,
    val isPublic: Boolean,
    val perks: List<GuildPerk>
)

data class GuildPerk(
    val type: GuildPerkType,
    val value: Float,
    val description: String
)

enum class GuildPerkType {
    XP_BOOST, COIN_BOOST, STREAK_PROTECTION, EXTRA_LIVES, HINT_DISCOUNT
}

data class SeasonalRank(
    val tier: RankTier = RankTier.BRONZE,
    val division: Int = 5,
    val points: Int = 0,
    val seasonId: String = "",
    val peakTier: RankTier = RankTier.BRONZE,
    val peakDivision: Int = 5
)

enum class RankTier(val displayName: String, val color: Color, val icon: String) {
    BRONZE("Bronze", Color(0xFFCD7F32), "🥉"),
    SILVER("Silver", Color(0xFFC0C0C0), "🥈"),
    GOLD("Gold", Color(0xFFFFD700), "🥇"),
    PLATINUM("Platinum", Color(0xFFE5E4E2), "💎"),
    DIAMOND("Diamond", Color(0xFFB9F2FF), "💠"),
    MASTER("Master", Color(0xFF9370DB), "👑"),
    GRANDMASTER("Grandmaster", Color(0xFFFF1493), "🔥")
}

data class UserPreferences(
    val preferredCategories: List<GameCategory> = emptyList(),
    val difficultyLevel: DifficultyLevel = DifficultyLevel.MIXED,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val vibrationsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val profileVisibility: ProfileVisibility = ProfileVisibility.PUBLIC,
    val autoMatchmaking: Boolean = false,
    val preferredGameMode: GameMode = GameMode.SINGLE_PLAYER
)

enum class UserRank(val displayName: String, val minLevel: Int, val color: String) {
    BEGINNER("Beginner", 1, "#4CAF50"),
    NOVICE("Novice", 5, "#2196F3"),
    INTERMEDIATE("Intermediate", 10, "#FF9800"),
    ADVANCED("Advanced", 20, "#9C27B0"),
    EXPERT("Expert", 50, "#F44336"),
    MASTER("Master", 100, "#FFD700")
}

enum class ProfileVisibility {
    PUBLIC, FRIENDS_ONLY, PRIVATE
}

data class UserLevel(
    val currentLevel: Int,
    val levelName: String,
    val currentXP: Long,
    val xpToNextLevel: Long,
    val progressPercentage: Float,
    val levelColor: String,
    val unlockedFeatures: List<String>
)

data class Question(
    val id: String,
    val text: String,
    val category: GameCategory,
    val difficulty: DifficultyLevel,
    val type: QuestionType,
    val options: List<String> = emptyList(),
    val correctAnswer: String,
    val explanation: String? = null,
    val points: Int,
    val timeLimit: Int = 30, // seconds
    val mediaUrl: String? = null
)

enum class QuestionType {
    MULTIPLE_CHOICE, TRUE_FALSE, FILL_IN_BLANK, MATCHING
}

enum class DifficultyLevel(val displayName: String, val multiplier: Float, val color: String) {
    EASY("Easy", 1.0f, "#4CAF50"),
    MEDIUM("Medium", 1.5f, "#FF9800"),
    HARD("Hard", 2.0f, "#F44336"),
    EXPERT("Expert", 3.0f, "#9C27B0"),
    MIXED("Mixed", 1.0f, "#2196F3")
}

enum class GameCategory(val displayName: String, val icon: String) {
    GENERAL_KNOWLEDGE("General Knowledge", "🧠"),
    SCIENCE("Science", "🔬"),
    HISTORY("History", "📚"),
    GEOGRAPHY("Geography", "🌍"),
    SPORTS("Sports", "⚽"),
    ENTERTAINMENT("Entertainment", "🎬"),
    ART("Art & Literature", "🎨"),
    TECHNOLOGY("Technology", "💻"),
    MUSIC("Music", "🎵"),
    MOVIES("Movies & TV", "🎭"),
    FOOD("Food & Drinks", "🍕"),
    NATURE("Nature & Animals", "🦎")
}

data class Game(
    val id: String,
    val mode: GameMode,
    val category: GameCategory,
    val difficulty: DifficultyLevel,
    val questions: List<Question>,
    val players: List<Player>,
    val currentQuestionIndex: Int = 0,
    val timePerQuestion: Int = 30,
    val status: GameStatus = GameStatus.WAITING,
    val createdAt: String,
    val startedAt: String? = null,
    val endedAt: String? = null,
    val settings: GameSettings = GameSettings()
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    val isFinished: Boolean
        get() = status == GameStatus.FINISHED

    val totalQuestions: Int
        get() = questions.size
}

data class GameSettings(
    val questionsCount: Int = 10,
    val timePerQuestion: Int = 30,
    val allowHints: Boolean = true,
    val powerUpsEnabled: Boolean = true,
    val isRanked: Boolean = false
)

enum class GameMode(val displayName: String, val maxPlayers: Int) {
    SINGLE_PLAYER("Single Player", 1),
    QUICK_MATCH("Quick Match", 2),
    MULTIPLAYER("Multiplayer", 6),
    TOURNAMENT("Tournament", 16),
    DAILY_CHALLENGE("Daily Challenge", 1),
    SURVIVAL("Survival Mode", 1)
}

enum class GameStatus {
    WAITING, IN_PROGRESS, PAUSED, FINISHED, CANCELLED
}

data class Player(
    val user: User,
    val score: Long = 0,
    val correctAnswers: Int = 0,
    val streak: Int = 0,
    val timeBonus: Long = 0,
    val powerUpsUsed: Int = 0,
    val isReady: Boolean = false,
    val answers: List<PlayerAnswer> = emptyList()
) {
    val accuracy: Double
        get() = if (answers.isNotEmpty())
            (correctAnswers.toDouble() / answers.size.toDouble()) * 100
        else 0.0
}

data class PlayerAnswer(
    val questionId: String,
    val answer: String,
    val isCorrect: Boolean,
    val timeSpent: Int, // milliseconds
    val pointsEarned: Long,
    val answeredAt: String
)

data class GameResult(
    val game: Game,
    val player: Player,
    val finalScore: Long,
    val rank: Int,
    val xpEarned: Long,
    val coinsEarned: Long,
    val achievementsUnlocked: List<Achievement>,
    val statistics: GameStatistics
)

data class GameStatistics(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val averageTimePerQuestion: Int,
    val fastestAnswer: Int,
    val longestStreak: Int,
    val categoryBreakdown: Map<GameCategory, Int>
)

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String,
    val rarity: AchievementRarity,
    val category: AchievementCategory,
    val isUnlocked: Boolean = false,
    val unlockedAt: String? = null,
    val progress: Int = 0,
    val maxProgress: Int = 1,
    val xpReward: Long = 0,
    val coinReward: Long = 0
) {
    val progressPercentage: Float
        get() = if (maxProgress > 0) (progress.toFloat() / maxProgress.toFloat()) * 100f else 0f
}

enum class AchievementRarity(val displayName: String, val color: String) {
    COMMON("Common", "#9E9E9E"),
    UNCOMMON("Uncommon", "#4CAF50"),
    RARE("Rare", "#2196F3"),
    EPIC("Epic", "#9C27B0"),
    LEGENDARY("Legendary", "#FFD700")
}

enum class AchievementCategory {
    GAMEPLAY, SOCIAL, PROGRESSION, SPECIAL_EVENTS, KNOWLEDGE
}

data class DailyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val category: GameCategory,
    val difficulty: DifficultyLevel,
    val requirements: ChallengeRequirements,
    val reward: ChallengeReward,
    val progress: Int = 0,
    val maxProgress: Int,
    val isCompleted: Boolean = false,
    val expiresAt: String
) {
    val progressPercentage: Float
        get() = if (maxProgress > 0) (progress.toFloat() / maxProgress.toFloat()) * 100f else 0f
}

data class ChallengeRequirements(
    val gamesCount: Int = 0,
    val correctAnswers: Int = 0,
    val streak: Int = 0,
    val category: GameCategory? = null,
    val difficulty: DifficultyLevel? = null
)

data class ChallengeReward(
    val type: RewardType,
    val amount: Long,
    val description: String
)

enum class RewardType {
    XP, COINS, GEMS, AVATAR, BADGE, POWER_UP, TITLE, CHEST, LOOT_BOX
}

data class PowerUp(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String,
    val type: PowerUpType,
    val duration: Int = 0, // seconds, 0 for instant
    val uses: Int = 1,
    val coinCost: Long = 0,
    val gemCost: Long = 0,
    val rarity: RewardRarity = RewardRarity.COMMON,
    val cooldown: Long = 0, // seconds
    val isUnlocked: Boolean = true
)

enum class PowerUpType {
    SKIP_QUESTION,
    FIFTY_FIFTY,
    DOUBLE_XP,
    EXTRA_TIME,
    FREEZE_TIME,
    HINT,
    DOUBLE_COINS,
    STREAK_SHIELD,
    SECOND_CHANCE,
    CATEGORY_BOOST,
    LUCKY_GUESS,
    MULTIPLIER_BOOST
}

data class WeeklyStats(
    val rank: Int,
    val totalPlayers: Int,
    val gamesPlayed: Int,
    val xpEarned: Long,
    val streak: Int,
    val weekStartDate: String
)

data class GameInvitation(
    val id: String,
    val fromUser: User,
    val toUser: User,
    val gameMode: GameMode,
    val category: GameCategory,
    val difficulty: DifficultyLevel,
    val message: String? = null,
    val createdAt: String,
    val expiresAt: String,
    val status: InvitationStatus = InvitationStatus.PENDING,
    val wager: Wager? = null // Optional betting system
)

data class Wager(
    val amount: Long,
    val currency: WagerCurrency,
    val isAccepted: Boolean = false
)

enum class WagerCurrency {
    COINS, GEMS, XP
}

data class LeaderboardEntry(
    val user: User,
    val rank: Int,
    val score: Long,
    val gamesPlayed: Int,
    val winRate: Double,
    val changeFromLastWeek: Int = 0 // +/- position change
)

data class Tournament(
    val id: String,
    val name: String,
    val description: String,
    val category: GameCategory,
    val difficulty: DifficultyLevel,
    val entryFee: Long,
    val prizePool: Long,
    val maxParticipants: Int,
    val currentParticipants: Int,
    val startTime: String,
    val endTime: String,
    val status: TournamentStatus,
    val prizes: List<TournamentPrize>,
    val rules: TournamentRules
)

data class TournamentPrize(
    val position: Int,
    val rewards: List<Reward>
)

data class TournamentRules(
    val questionsPerMatch: Int,
    val timePerQuestion: Int,
    val eliminationStyle: Boolean,
    val powerUpsAllowed: Boolean
)

enum class TournamentStatus {
    UPCOMING, REGISTRATION_OPEN, IN_PROGRESS, FINISHED, CANCELLED
}

data class Shop(
    val powerUps: List<PowerUp>,
    val cosmetics: List<CosmeticItem>,
    val lootBoxes: List<LootBox>,
    val featuredDeals: List<Deal>
)

data class CosmeticItem(
    val id: String,
    val name: String,
    val description: String,
    val type: CosmeticType,
    val rarity: RewardRarity,
    val coinCost: Long = 0,
    val gemCost: Long = 0,
    val imageUrl: String,
    val isOwned: Boolean = false,
    val isEquipped: Boolean = false
)

enum class CosmeticType {
    AVATAR_FRAME, AVATAR_BADGE, PROFILE_THEME, ANSWER_ANIMATION, VICTORY_DANCE
}

data class LootBox(
    val id: String,
    val name: String,
    val description: String,
    val coinCost: Long,
    val gemCost: Long,
    val contents: List<LootBoxItem>,
    val guaranteedRarity: RewardRarity,
    val imageUrl: String
)

data class LootBoxItem(
    val reward: Reward,
    val dropChance: Float // 0.0 to 1.0
)

data class Deal(
    val id: String,
    val title: String,
    val description: String,
    val originalPrice: Long,
    val discountPrice: Long,
    val discountPercentage: Int,
    val currency: WagerCurrency,
    val item: Any, // Can be PowerUp, CosmeticItem, etc.
    val expiresAt: String,
    val isLimitedTime: Boolean = true
)

enum class InvitationStatus {
    PENDING, ACCEPTED, DECLINED, EXPIRED
}

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val type: QuestType,
    val requirements: QuestRequirements,
    val rewards: List<Reward>,
    val progress: Int = 0,
    val maxProgress: Int,
    val isCompleted: Boolean = false,
    val expiresAt: String? = null, // null for permanent quests
    val difficulty: DifficultyLevel = DifficultyLevel.EASY
) {
    val progressPercentage: Float
        get() = if (maxProgress > 0) (progress.toFloat() / maxProgress.toFloat()) * 100f else 0f
}

data class QuestRequirements(
    val gamesWon: Int = 0,
    val questionsAnswered: Int = 0,
    val streakAchieved: Int = 0,
    val categoriesPlayed: List<GameCategory> = emptyList(),
    val difficultyLevel: DifficultyLevel? = null,
    val socialAction: SocialActionType? = null
)

enum class QuestType {
    DAILY, WEEKLY, SEASONAL, ACHIEVEMENT, SOCIAL
}

enum class SocialActionType {
    CHALLENGE_FRIEND, HELP_FRIEND, JOIN_GUILD, INVITE_PLAYER
}

data class Reward(
    val type: RewardType,
    val amount: Long,
    val description: String,
    val rarity: RewardRarity = RewardRarity.COMMON
)

enum class RewardRarity(val color: Long, val multiplier: Float) {
    COMMON(0xFF9E9E9E, 1.0f),
    UNCOMMON(0xFF4CAF50, 1.2f),
    RARE(0xFF2196F3, 1.5f),
    EPIC(0xFF9C27B0, 2.0f),
    LEGENDARY(0xFFFFD700, 3.0f)
}