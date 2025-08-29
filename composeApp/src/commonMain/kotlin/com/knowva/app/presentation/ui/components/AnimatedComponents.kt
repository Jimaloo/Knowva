package com.knowva.app.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.knowva.app.presentation.ui.theme.*

/**
 * Animated progress bar with optional percentage display
 */
@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = TriviaColors.SurfaceVariant,
    progressColor: Color = TriviaColors.XPGreen,
    animationSpec: AnimationSpec<Float> = AnimationConstants.SPRING_SPEC,
    showPercentage: Boolean = true,
    height: androidx.compose.ui.unit.Dp = Dimensions.ProgressBarHeight
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = animationSpec,
        label = "progress"
    )

    Column(modifier = modifier) {
        if (showPercentage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = TriviaTypography.LabelLarge,
                    color = TriviaColors.OnSurface
                )
            }
            Spacer(modifier = Modifier.height(Dimensions.SpacingExtraSmall))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(backgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(height / 2))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(progressColor, progressColor.alpha(0.8f))
                        )
                    )
                    .animateContentSize()
            )
        }
    }
}

/**
 * Animated level badge with glow effect
 */
@Composable
fun AnimatedLevelBadge(
    level: Int,
    color: Color,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = Dimensions.IconExtraLarge
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = glowAlpha),
                        color.copy(alpha = 0.3f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = level.toString(),
            style = TriviaTypography.TitleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Circular progress indicator for XP or game progress
 */
@Composable
fun CircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: androidx.compose.ui.unit.Dp = 6.dp,
    backgroundColor: Color = TriviaColors.SurfaceVariant,
    progressColor: Color = TriviaColors.XPGreen,
    size: androidx.compose.ui.unit.Dp = 80.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = AnimationConstants.SPRING_SPEC,
        label = "circular_progress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidth.toPx()
            val radius = (size.toPx() - strokeWidthPx) / 2
            val center = androidx.compose.ui.geometry.Offset(
                this.size.width / 2f,
                this.size.height / 2f
            )

            // Background circle
            drawCircle(
                color = backgroundColor,
                radius = radius,
                center = center,
                style = Stroke(strokeWidthPx)
            )

            // Progress arc
            if (animatedProgress > 0f) {
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        center.x - radius,
                        center.y - radius
                    ),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(
                        width = strokeWidthPx,
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            style = TriviaTypography.LabelLarge,
            color = TriviaColors.OnSurface,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Feature chip for displaying unlocked features
 */
@Composable
fun FeatureChip(
    text: String,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isUnlocked) TriviaColors.Success else TriviaColors.OnSurfaceVariant
    val textColor = if (isUnlocked) Color.White else TriviaColors.OnSurface

    Surface(
        modifier = modifier,
        shape = TriviaShapes.Badge,
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = Dimensions.SpacingMedium,
                vertical = Dimensions.SpacingSmall
            ),
            style = TriviaTypography.LabelLarge,
            color = textColor
        )
    }
}

/**
 * Streak indicator with fire animation
 */
@Composable
fun StreakIndicator(
    streak: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fire")
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fire_scale"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
    ) {
        Text(
            text = "🔥",
            modifier = Modifier.graphicsLayer(scaleX = fireScale, scaleY = fireScale),
            style = TriviaTypography.TitleLarge
        )

        Text(
            text = "$streak Streak",
            style = TriviaTypography.TitleMedium,
            color = TriviaColors.StreakFire,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Shimmer effect for loading states
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    color: Color = TriviaColors.SurfaceVariant
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    Box(
        modifier = modifier
            .background(color.copy(alpha = alpha))
            .clip(TriviaShapes.Small)
    )
}

/**
 * Floating action button with pulse animation
 */
@Composable
fun PulseFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.graphicsLayer(scaleX = scale, scaleY = scale),
        containerColor = TriviaColors.Primary
    ) {
        content()
    }
}