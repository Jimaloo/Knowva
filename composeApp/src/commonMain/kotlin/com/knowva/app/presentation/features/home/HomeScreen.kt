package com.knowva.app.presentation.features.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.knowva.app.presentation.ui.theme.*
import com.knowva.app.presentation.ui.components.*
import com.knowva.app.domain.entities.*

/**
 * Home Screen with MVI pattern
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToQuickMatch: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToLeaderboards: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    // Handle side effects
    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                HomeSideEffect.NavigateToQuickMatch -> onNavigateToQuickMatch()
                HomeSideEffect.NavigateToProfile -> onNavigateToProfile()
                HomeSideEffect.NavigateToLeaderboards -> onNavigateToLeaderboards()
                HomeSideEffect.NavigateToAchievements -> onNavigateToAchievements()
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = TriviaColors.Primary
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Dimensions.SpacingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
            ) {
                // Welcome Section with Currency Display
                item {
                    WelcomeSection(
                        user = state.user,
                        userLevel = state.userLevel,
                        onProfileClick = {
                            viewModel.handleIntent(HomeIntent.OpenProfile)
                        }
                    )
                }

                // Quick Actions
                item {
                    QuickActionsSection(
                        onQuickMatch = {
                            viewModel.handleIntent(HomeIntent.StartQuickMatch)
                        },
                        onDailyChallenge = {
                            viewModel.handleIntent(HomeIntent.OpenDailyChallenge)
                        }
                    )
                }

                // Daily Challenge (only if exists)
                state.dailyChallenge?.let { challenge ->
                    item {
                        DailyChallengeCard(
                            challenge = challenge,
                            onClick = {
                                viewModel.handleIntent(HomeIntent.OpenDailyChallenge)
                            }
                        )
                    }
                }

                // Weekly Progress (simplified)
                state.weeklyStats?.let { weeklyStats ->
                    item {
                        SimpleStatsCard(weeklyStats = weeklyStats)
                    }
                }
            }
        }

        // Error handling
        state.error?.let { error ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(Dimensions.SpacingMedium),
                colors = CardDefaults.cardColors(
                    containerColor = TriviaColors.Error
                )
            ) {
                Row(
                    modifier = Modifier.padding(Dimensions.SpacingMedium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.weight(1f),
                        color = Color.White
                    )
                    TextButton(
                        onClick = { viewModel.handleIntent(HomeIntent.DismissError) }
                    ) {
                        Text("Dismiss", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeSection(
    user: User?,
    userLevel: UserLevel?,
    onProfileClick: () -> Unit
) {
    user?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onProfileClick() },
            shape = TriviaShapes.Large,
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.SpacingLarge)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // User Avatar Placeholder
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(TriviaColors.Primary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.displayName.take(2).uppercase(),
                            style = TriviaTypography.TitleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(Dimensions.SpacingMedium))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Welcome back,",
                            style = TriviaTypography.BodyLarge,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = user.displayName,
                            style = TriviaTypography.HeadlineMedium,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        userLevel?.let { level ->
                            Text(
                                text = "Level ${level.currentLevel}",
                                style = TriviaTypography.LabelLarge,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Currency Display (integrated)
                Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
                ) {
                    CurrencyItem(
                        amount = user.coins,
                        icon = "🪙",
                        color = TriviaColors.LevelGold,
                        modifier = Modifier.weight(1f)
                    )
                    CurrencyItem(
                        amount = user.gems,
                        icon = "💎",
                        color = TriviaColors.Info,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    onQuickMatch: () -> Unit,
    onDailyChallenge: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
    ) {
        QuickActionButton(
            modifier = Modifier.weight(1f),
            text = "Quick Match",
            backgroundColor = TriviaColors.Primary,
            onClick = onQuickMatch
        )

        QuickActionButton(
            modifier = Modifier.weight(1f),
            text = "Daily Challenge",
            backgroundColor = TriviaColors.Secondary,
            onClick = onDailyChallenge
        )
    }
}

@Composable
fun QuickActionButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(Dimensions.ButtonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = TriviaShapes.Medium
    ) {
        Text(
            text = text,
            style = TriviaTypography.LabelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DailyChallengeCard(
    challenge: DailyChallenge,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = TriviaShapes.Medium,
        colors = CardDefaults.cardColors(
            containerColor = TriviaColors.LevelGold.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, TriviaColors.LevelGold.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.SpacingMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Trophy icon placeholder
                Box(
                    modifier = Modifier
                        .size(Dimensions.IconLarge)
                        .clip(CircleShape)
                        .background(TriviaColors.LevelGold.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏆",
                        style = TriviaTypography.TitleMedium
                    )
                }

                Spacer(modifier = Modifier.width(Dimensions.SpacingMedium))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = challenge.title,
                        style = TriviaTypography.TitleLarge,
                        color = Color.White
                    )
                    Text(
                        text = challenge.description,
                        style = TriviaTypography.BodyLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                if (challenge.isCompleted) {
                    Box(
                        modifier = Modifier
                            .size(Dimensions.IconMedium)
                            .clip(CircleShape)
                            .background(TriviaColors.Success),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

            AnimatedProgressBar(
                progress = challenge.progressPercentage / 100f,
                progressColor = TriviaColors.LevelGold
            )

            Spacer(modifier = Modifier.height(Dimensions.SpacingSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progress: ${challenge.progress}/${challenge.maxProgress}",
                    style = TriviaTypography.LabelLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Text(
                    text = "Reward: ${challenge.reward.amount} ${challenge.reward.type.name}",
                    style = TriviaTypography.LabelLarge,
                    color = TriviaColors.LevelGold
                )
            }
        }
    }
}

@Composable
fun WeeklyProgressCard(
    weeklyStats: WeeklyStats,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = TriviaShapes.Medium,
        colors = CardDefaults.cardColors(
            containerColor = TriviaColors.Info.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, TriviaColors.Info.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.SpacingMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimensions.IconLarge)
                        .clip(CircleShape)
                        .background(TriviaColors.Info.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📈",
                        style = TriviaTypography.TitleMedium
                    )
                }

                Spacer(modifier = Modifier.width(Dimensions.SpacingMedium))

                Column {
                    Text(
                        text = "This Week's Progress",
                        style = TriviaTypography.TitleLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Rank #${weeklyStats.rank} of ${weeklyStats.totalPlayers}",
                        style = TriviaTypography.BodyLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeeklyStatItem(
                    label = "Games",
                    value = weeklyStats.gamesPlayed.toString(),
                    emoji = "🎮"
                )

                WeeklyStatItem(
                    label = "XP Earned",
                    value = weeklyStats.xpEarned.toString(),
                    emoji = "⭐"
                )

                WeeklyStatItem(
                    label = "Streak",
                    value = weeklyStats.streak.toString(),
                    emoji = "🔥"
                )
            }
        }
    }
}

@Composable
fun WeeklyStatItem(
    label: String,
    value: String,
    emoji: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            style = TriviaTypography.TitleMedium
        )
        Spacer(modifier = Modifier.height(Dimensions.SpacingExtraSmall))
        Text(
            text = value,
            style = TriviaTypography.HeadlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = TriviaTypography.LabelLarge,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun AchievementCard(
    achievement: Achievement,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val rarityColor = when (achievement.rarity) {
        AchievementRarity.COMMON -> TriviaColors.OnSurfaceVariant
        AchievementRarity.UNCOMMON -> TriviaColors.Success
        AchievementRarity.RARE -> TriviaColors.Info
        AchievementRarity.EPIC -> TriviaColors.Secondary
        AchievementRarity.LEGENDARY -> TriviaColors.LevelGold
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = TriviaShapes.Medium,
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked)
                TriviaColors.SurfaceVariant else
                TriviaColors.SurfaceVariant.copy(alpha = 0.5f)
        ),
        border = if (achievement.isUnlocked)
            BorderStroke(2.dp, rarityColor) else null
    ) {
        Row(
            modifier = Modifier.padding(Dimensions.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Achievement Icon Placeholder
            Box(
                modifier = Modifier
                    .size(Dimensions.IconExtraLarge)
                    .clip(CircleShape)
                    .background(rarityColor.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏅",
                    style = TriviaTypography.TitleLarge
                )
            }

            Spacer(modifier = Modifier.width(Dimensions.SpacingMedium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.name,
                    style = TriviaTypography.TitleLarge,
                    color = if (achievement.isUnlocked)
                        TriviaColors.OnSurface else
                        TriviaColors.OnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = achievement.description,
                    style = TriviaTypography.BodyLarge,
                    color = TriviaColors.OnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!achievement.isUnlocked && achievement.maxProgress > 1) {
                    Spacer(modifier = Modifier.height(Dimensions.SpacingSmall))
                    AnimatedProgressBar(
                        progress = achievement.progressPercentage / 100f,
                        progressColor = rarityColor,
                        showPercentage = false
                    )
                    Text(
                        text = "${achievement.progress}/${achievement.maxProgress}",
                        style = TriviaTypography.LabelLarge,
                        color = TriviaColors.OnSurfaceVariant
                    )
                }
            }

            if (achievement.isUnlocked) {
                Box(
                    modifier = Modifier
                        .size(Dimensions.IconMedium)
                        .clip(CircleShape)
                        .background(rarityColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleStatsCard(
    weeklyStats: WeeklyStats
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = TriviaShapes.Medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.SpacingMedium)
        ) {
            Text(
                text = "This Week",
                style = TriviaTypography.TitleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Dimensions.SpacingSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SimpleStatItem(
                    label = "Rank",
                    value = "#${weeklyStats.rank}"
                )
                SimpleStatItem(
                    label = "Games",
                    value = weeklyStats.gamesPlayed.toString()
                )
                SimpleStatItem(
                    label = "XP",
                    value = weeklyStats.xpEarned.toString()
                )
            }
        }
    }
}

@Composable
fun SimpleStatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = TriviaTypography.TitleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = TriviaTypography.LabelMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}