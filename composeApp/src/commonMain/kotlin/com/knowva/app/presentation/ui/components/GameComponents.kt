package com.knowva.app.presentation.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.knowva.app.presentation.ui.theme.*
import com.knowva.app.domain.entities.*

/**
 * Simple Game Components for the Home Screen
 * Using only basic Compose components without external dependencies
 */

@Composable
fun SimpleGameCard(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = TriviaShapes.Medium,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(1.dp, backgroundColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.SpacingMedium)
        ) {
            Text(
                text = title,
                style = TriviaTypography.TitleLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(Dimensions.SpacingSmall))

            Text(
                text = subtitle,
                style = TriviaTypography.BodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}