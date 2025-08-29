package com.knowva.app.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.knowva.app.domain.entities.*
import com.knowva.app.presentation.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Enhanced gamification components for a more engaging user experience
 */

@Composable
fun CurrencyDisplay(
    coins: Long,
    gems: Long,
    modifier: Modifier = Modifier,
    showAnimations: Boolean = true
) {
    var coinsVisible by remember { mutableStateOf(!showAnimations) }
    var gemsVisible by remember { mutableStateOf(!showAnimations) }

    LaunchedEffect(showAnimations) {
        if (showAnimations) {
            delay(200)
            coinsVisible = true
            delay(100)
            gemsVisible = true
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
    ) {
        // Coins
        AnimatedVisibility(
            visible = coinsVisible,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
        ) {
            CurrencyItem(
                amount = coins,
                icon = "🪙",
                color = TriviaColors.LevelGold
            )
        }

        // Gems
        AnimatedVisibility(
            visible = gemsVisible,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
        ) {
            CurrencyItem(
                amount = gems,
                icon = "💎",
                color = TriviaColors.Info
            )
        }
    }
}

@Composable
fun CurrencyItem(
    amount: Long,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = TriviaShapes.Badge,
        color = color.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Dimensions.SpacingMedium,
                vertical = Dimensions.SpacingSmall
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
        ) {
            Text(
                text = icon,
                style = TriviaTypography.LabelLarge
            )
            Text(
                text = formatNumber(amount),
                style = TriviaTypography.LabelLarge,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun XPRewardAnimation(
    xpGained: Long,
    isVisible: Boolean,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1.2f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { onAnimationEnd() },
        label = "xp_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "xp_alpha"
    )

    Box(
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = TriviaShapes.Medium,
            color = TriviaColors.XPGreen,
            shadowElevation = 8.dp
        ) {
            Text(
                text = "+$xpGained XP",
                modifier = Modifier.padding(Dimensions.SpacingMedium),
                style = TriviaTypography.TitleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PowerUpCard(
    powerUp: PowerUp,
    isSelected: Boolean = false,
    isUsable: Boolean = true,
    count: Int = 0,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rarityColor = getRarityColor(powerUp.rarity)
    val borderColor = when {
        isSelected -> TriviaColors.Primary
        isUsable -> rarityColor
        else -> TriviaColors.OnSurfaceVariant.copy(alpha = 0.3f)
    }

    val backgroundColor = when {
        isUsable -> TriviaColors.SurfaceVariant
        else -> TriviaColors.SurfaceVariant.copy(alpha = 0.5f)
    }

    Card(
        modifier = modifier
            .aspectRatio(0.8f)
            .clickable(enabled = isUsable) { onClick() },
        shape = TriviaShapes.Medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(
            width = if (isSelected) 3.dp else 1.dp,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimensions.SpacingMedium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Power-up icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(rarityColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getPowerUpIcon(powerUp.type),
                        style = TriviaTypography.HeadlineMedium
                    )
                }

                // Name
                Text(
                    text = powerUp.name,
                    style = TriviaTypography.LabelLarge,
                    color = if (isUsable) TriviaColors.OnSurface else TriviaColors.OnSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Cost or count
                if (count > 0) {
                    Surface(
                        shape = CircleShape,
                        color = TriviaColors.Primary
                    ) {
                        Text(
                            text = count.toString(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = TriviaTypography.LabelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (powerUp.coinCost > 0) {
                            Text(text = "🪙", style = TriviaTypography.LabelMedium)
                            Text(
                                text = powerUp.coinCost.toString(),
                                style = TriviaTypography.LabelMedium,
                                color = TriviaColors.LevelGold
                            )
                        }
                        if (powerUp.gemCost > 0) {
                            Text(text = "💎", style = TriviaTypography.LabelMedium)
                            Text(
                                text = powerUp.gemCost.toString(),
                                style = TriviaTypography.LabelMedium,
                                color = TriviaColors.Info
                            )
                        }
                    }
                }
            }

            // Rarity indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(rarityColor)
            )
        }
    }
}

@Composable
fun PowerUpInventory(
    powerUps: Map<PowerUpType, Int>,
    availablePowerUps: List<PowerUp>,
    onPowerUpSelected: (PowerUp) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Dimensions.SpacingMedium),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
    ) {
        items(availablePowerUps) { powerUp ->
            val count = powerUps[powerUp.type] ?: 0
            PowerUpCard(
                powerUp = powerUp,
                count = count,
                isUsable = count > 0,
                onClick = { if (count > 0) onPowerUpSelected(powerUp) }
            )
        }
    }
}

@Composable
fun AchievementNotification(
    achievement: Achievement,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rarityColor = getAchievementRarityColor(achievement.rarity)

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onDismiss() },
            shape = TriviaShapes.Medium,
            colors = CardDefaults.cardColors(
                containerColor = rarityColor.copy(alpha = 0.15f)
            ),
            border = BorderStroke(2.dp, rarityColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(Dimensions.SpacingMedium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
            ) {
                // Achievement icon with glow effect
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    rarityColor.copy(alpha = 0.3f),
                                    rarityColor.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏆",
                        style = TriviaTypography.HeadlineMedium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Achievement Unlocked!",
                        style = TriviaTypography.LabelLarge,
                        color = rarityColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = achievement.name,
                        style = TriviaTypography.TitleMedium,
                        color = TriviaColors.OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    if (achievement.xpReward > 0 || achievement.coinReward > 0) {
                        Text(
                            text = buildString {
                                if (achievement.xpReward > 0) append("${achievement.xpReward} XP")
                                if (achievement.coinReward > 0) {
                                    if (achievement.xpReward > 0) append(" • ")
                                    append("${achievement.coinReward} coins")
                                }
                            },
                            style = TriviaTypography.LabelMedium,
                            color = TriviaColors.OnSurfaceVariant
                        )
                    }
                }

                // Rarity badge
                Surface(
                    shape = TriviaShapes.Small,
                    color = rarityColor
                ) {
                    Text(
                        text = achievement.rarity.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = TriviaTypography.LabelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QuestCard(
    quest: Quest,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressColor = when (quest.difficulty) {
        DifficultyLevel.EASY -> TriviaColors.EasyGreen
        DifficultyLevel.MEDIUM -> TriviaColors.MediumYellow
        DifficultyLevel.HARD -> TriviaColors.HardRed
        DifficultyLevel.EXPERT -> TriviaColors.ExpertPurple
        DifficultyLevel.MIXED -> TriviaColors.Info
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = TriviaShapes.Medium,
        colors = CardDefaults.cardColors(
            containerColor = if (quest.isCompleted)
                TriviaColors.Success.copy(alpha = 0.1f)
            else TriviaColors.SurfaceVariant
        ),
        border = if (quest.isCompleted)
            BorderStroke(1.dp, TriviaColors.Success)
        else null
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.SpacingMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quest.title,
                        style = TriviaTypography.TitleMedium,
                        color = TriviaColors.OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = quest.description,
                        style = TriviaTypography.BodyMedium,
                        color = TriviaColors.OnSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = TriviaShapes.Small,
                    color = progressColor
                ) {
                    Text(
                        text = quest.difficulty.displayName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = TriviaTypography.LabelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

            if (!quest.isCompleted) {
                AnimatedProgressBar(
                    progress = quest.progressPercentage / 100f,
                    progressColor = progressColor,
                    showPercentage = false
                )

                Spacer(modifier = Modifier.height(Dimensions.SpacingSmall))

                Text(
                    text = "${quest.progress}/${quest.maxProgress}",
                    style = TriviaTypography.LabelMedium,
                    color = TriviaColors.OnSurfaceVariant
                )
            }

            // Rewards
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
            ) {
                items(quest.rewards) { reward ->
                    RewardChip(reward = reward)
                }
            }
        }
    }
}

@Composable
fun RewardChip(
    reward: Reward,
    modifier: Modifier = Modifier
) {
    val rarityColor = getRarityColor(reward.rarity)

    Surface(
        modifier = modifier,
        shape = TriviaShapes.Small,
        color = rarityColor.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, rarityColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = when (reward.type) {
                    RewardType.XP -> "⭐"
                    RewardType.COINS -> "🪙"
                    RewardType.GEMS -> "💎"
                    RewardType.POWER_UP -> "⚡"
                    else -> "🎁"
                },
                style = TriviaTypography.LabelSmall
            )
            Text(
                text = "${reward.amount}",
                style = TriviaTypography.LabelSmall,
                color = rarityColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RankDisplay(
    rank: SeasonalRank,
    modifier: Modifier = Modifier
) {
    val tierColor = rank.tier.color

    Surface(
        modifier = modifier,
        shape = TriviaShapes.Medium,
        color = tierColor.copy(alpha = 0.2f),
        border = BorderStroke(2.dp, tierColor)
    ) {
        Row(
            modifier = Modifier.padding(Dimensions.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
        ) {
            Text(
                text = rank.tier.icon,
                style = TriviaTypography.HeadlineMedium
            )

            Column {
                Text(
                    text = rank.tier.displayName,
                    style = TriviaTypography.TitleMedium,
                    color = tierColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Division ${rank.division}",
                    style = TriviaTypography.LabelMedium,
                    color = TriviaColors.OnSurfaceVariant
                )
                Text(
                    text = "${rank.points} LP",
                    style = TriviaTypography.LabelMedium,
                    color = tierColor
                )
            }
        }
    }
}

// Helper functions
private fun getPowerUpIcon(type: PowerUpType): String {
    return when (type) {
        PowerUpType.SKIP_QUESTION -> "⏭️"
        PowerUpType.FIFTY_FIFTY -> "❌"
        PowerUpType.DOUBLE_XP -> "⚡"
        PowerUpType.EXTRA_TIME -> "⏰"
        PowerUpType.FREEZE_TIME -> "🧊"
        PowerUpType.HINT -> "💡"
        PowerUpType.DOUBLE_COINS -> "💰"
        PowerUpType.STREAK_SHIELD -> "🛡️"
        PowerUpType.SECOND_CHANCE -> "🔄"
        PowerUpType.CATEGORY_BOOST -> "📚"
        PowerUpType.LUCKY_GUESS -> "🍀"
        PowerUpType.MULTIPLIER_BOOST -> "🚀"
    }
}

private fun getRarityColor(rarity: RewardRarity): Color {
    return when (rarity) {
        RewardRarity.COMMON -> TriviaColors.OnSurfaceVariant
        RewardRarity.UNCOMMON -> TriviaColors.Success
        RewardRarity.RARE -> TriviaColors.Info
        RewardRarity.EPIC -> TriviaColors.Secondary
        RewardRarity.LEGENDARY -> TriviaColors.LevelGold
    }
}

private fun getAchievementRarityColor(rarity: AchievementRarity): Color {
    return when (rarity) {
        AchievementRarity.COMMON -> TriviaColors.OnSurfaceVariant
        AchievementRarity.UNCOMMON -> TriviaColors.Success
        AchievementRarity.RARE -> TriviaColors.Info
        AchievementRarity.EPIC -> TriviaColors.Secondary
        AchievementRarity.LEGENDARY -> TriviaColors.LevelGold
    }
}

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> {
            val millions = (number / 100_000L) / 10.0
            "${millions}M"
        }
        number >= 1_000 -> {
            val thousands = (number / 100L) / 10.0
            "${thousands}K"
        }
        else -> number.toString()
    }
}