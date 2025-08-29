package com.knowva.app.presentation.ui.theme

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Comprehensive design system for the gamified trivia app
 */

@Immutable
object TriviaColors {
    // Primary Colors
    val Primary = Color(0xFF6C5CE7)
    val PrimaryVariant = Color(0xFF5A4FCF)
    val Secondary = Color(0xFF00CEC9)
    val SecondaryVariant = Color(0xFF00B3B0)

    // Gamification Colors
    val XPGreen = Color(0xFF00E676)
    val LevelGold = Color(0xFFFFD700)
    val AchievementBronze = Color(0xFFCD7F32)
    val AchievementSilver = Color(0xFFC0C0C0)
    val AchievementGold = Color(0xFFFFD700)
    val StreakFire = Color(0xFFFF6B35)

    // Difficulty Colors
    val EasyGreen = Color(0xFF4CAF50)
    val MediumYellow = Color(0xFFFF9800)
    val HardRed = Color(0xFFF44336)
    val ExpertPurple = Color(0xFF9C27B0)

    // Status Colors
    val Success = Color(0xFF4CAF50)
    val Error = Color(0xFFF44336)
    val Warning = Color(0xFFFF9800)
    val Info = Color(0xFF2196F3)

    // Gradient Colors
    val GradientStart = Color(0xFF667eea)
    val GradientEnd = Color(0xFF764ba2)

    // Background
    val Surface = Color(0xFF121212)
    val SurfaceVariant = Color(0xFF1E1E1E)
    val OnSurface = Color(0xFFFFFFFF)
    val OnSurfaceVariant = Color(0xFFB3B3B3)

    // Category Colors
    val GeneralKnowledge = Color(0xFF9C27B0)
    val Science = Color(0xFF2196F3)
    val History = Color(0xFF795548)
    val Geography = Color(0xFF4CAF50)
    val Sports = Color(0xFFFF9800)
    val Entertainment = Color(0xFFE91E63)
    val Art = Color(0xFF673AB7)
    val Technology = Color(0xFF607D8B)
    val Music = Color(0xFF3F51B5)
    val Movies = Color(0xFFF44336)
    val Food = Color(0xFFFF5722)
    val Nature = Color(0xFF8BC34A)
}

@Immutable
object TriviaTypography {
    val HeadlineLarge = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    )

    val HeadlineMedium = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )

    val TitleLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp,
        letterSpacing = 0.15.sp
    )

    val TitleMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )

    val BodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

    val BodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )

    val LabelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )

    val LabelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

    val LabelSmall = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )

    val CaptionLarge = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
}

@Immutable
object TriviaShapes {
    val ExtraSmall = RoundedCornerShape(4.dp)
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(16.dp)
    val Large = RoundedCornerShape(24.dp)
    val ExtraLarge = RoundedCornerShape(32.dp)

    // Special shapes for gamification elements
    val Badge = RoundedCornerShape(50.dp)
    val Button = RoundedCornerShape(12.dp)
    val Card = RoundedCornerShape(20.dp)
}

@Immutable
object AnimationConstants {
    const val DURATION_SHORT = 150
    const val DURATION_MEDIUM = 300
    const val DURATION_LONG = 500
    const val DURATION_EXTRA_LONG = 1000

    // Spring animations for gamification elements
    val SPRING_SPEC = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    val TWEEN_SPEC = tween<Float>(
        durationMillis = DURATION_MEDIUM,
        easing = FastOutSlowInEasing
    )

    val ELASTIC_SPEC = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )

    // Achievement unlock animation
    val ACHIEVEMENT_UNLOCK = tween<Float>(
        durationMillis = DURATION_EXTRA_LONG,
        easing = EaseInOutQuad
    )
}

@Immutable
object Dimensions {
    val SpacingExtraSmall = 4.dp
    val SpacingSmall = 8.dp
    val SpacingMedium = 16.dp
    val SpacingLarge = 24.dp
    val SpacingExtraLarge = 32.dp

    val IconSmall = 16.dp
    val IconMedium = 24.dp
    val IconLarge = 32.dp
    val IconExtraLarge = 48.dp

    val ButtonHeight = 56.dp
    val CardElevation = 8.dp
    val ProgressBarHeight = 8.dp
}

/**
 * Extension functions for color manipulation
 */
fun Color.alpha(alpha: Float): Color = this.copy(alpha = alpha)

/**
 * Get category color by category type
 */
fun getCategoryColor(category: com.knowva.app.domain.entities.GameCategory): Color {
    return when (category) {
        com.knowva.app.domain.entities.GameCategory.GENERAL_KNOWLEDGE -> TriviaColors.GeneralKnowledge
        com.knowva.app.domain.entities.GameCategory.SCIENCE -> TriviaColors.Science
        com.knowva.app.domain.entities.GameCategory.HISTORY -> TriviaColors.History
        com.knowva.app.domain.entities.GameCategory.GEOGRAPHY -> TriviaColors.Geography
        com.knowva.app.domain.entities.GameCategory.SPORTS -> TriviaColors.Sports
        com.knowva.app.domain.entities.GameCategory.ENTERTAINMENT -> TriviaColors.Entertainment
        com.knowva.app.domain.entities.GameCategory.ART -> TriviaColors.Art
        com.knowva.app.domain.entities.GameCategory.TECHNOLOGY -> TriviaColors.Technology
        com.knowva.app.domain.entities.GameCategory.MUSIC -> TriviaColors.Music
        com.knowva.app.domain.entities.GameCategory.MOVIES -> TriviaColors.Movies
        com.knowva.app.domain.entities.GameCategory.FOOD -> TriviaColors.Food
        com.knowva.app.domain.entities.GameCategory.NATURE -> TriviaColors.Nature
    }
}

/**
 * Get difficulty color
 */
fun getDifficultyColor(difficulty: com.knowva.app.domain.entities.DifficultyLevel): Color {
    return when (difficulty) {
        com.knowva.app.domain.entities.DifficultyLevel.EASY -> TriviaColors.EasyGreen
        com.knowva.app.domain.entities.DifficultyLevel.MEDIUM -> TriviaColors.MediumYellow
        com.knowva.app.domain.entities.DifficultyLevel.HARD -> TriviaColors.HardRed
        com.knowva.app.domain.entities.DifficultyLevel.EXPERT -> TriviaColors.ExpertPurple
        com.knowva.app.domain.entities.DifficultyLevel.MIXED -> TriviaColors.Info
    }
}