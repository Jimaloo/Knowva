package com.knowva.app.presentation.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.knowva.app.presentation.mvi.HandleSideEffects
import com.knowva.app.presentation.ui.components.*
import com.knowva.app.presentation.ui.theme.*
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Handle side effects
    HandleSideEffects(
        sideEffectFlow = viewModel.sideEffect,
        onSideEffect = { effect ->
            when (effect) {
                ProfileContract.SideEffect.NavigateToAuth -> {
                    onNavigateToAuth()
                }

                is ProfileContract.SideEffect.ShowError -> {
                    // Error is already shown in the UI via state
                }

                is ProfileContract.SideEffect.ShowSuccess -> {
                    // Success messages could be shown via snackbar
                }
            }
        }
    )

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = TriviaColors.Primary)
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            TriviaColors.GradientStart.copy(alpha = 0.3f),
                            TriviaColors.Surface
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.SpacingMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AuthTextButton(
                    text = "← Back",
                    onClick = onNavigateBack
                )

                Text(
                    text = "Profile",
                    style = TriviaTypography.TitleLarge,
                    color = TriviaColors.OnSurface
                )

                AuthTextButton(
                    text = "Logout",
                    onClick = { viewModel.handleIntent(ProfileContract.Intent.Logout) }
                )
            }

            state.userProfile?.let { profile ->
                Column(
                    modifier = Modifier.padding(Dimensions.SpacingLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = TriviaColors.Surface
                        ),
                        shape = TriviaShapes.Large
                    ) {
                        Column(
                            modifier = Modifier.padding(Dimensions.SpacingLarge),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(TriviaColors.Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile.displayName.take(2).uppercase(),
                                    style = TriviaTypography.HeadlineMedium,
                                    color = TriviaColors.OnSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

                            if (state.isEditing) {
                                // Edit Mode
                                AuthTextField(
                                    value = state.displayName,
                                    onValueChange = {
                                        viewModel.handleIntent(
                                            ProfileContract.Intent.DisplayNameChanged(
                                                it
                                            )
                                        )
                                    },
                                    label = "Display Name",
                                    imeAction = ImeAction.Done
                                )

                                Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
                                ) {
                                    AuthSecondaryButton(
                                        text = "Cancel",
                                        onClick = {
                                            viewModel.handleIntent(ProfileContract.Intent.CancelEditing)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    AuthPrimaryButton(
                                        text = "Save",
                                        onClick = {
                                            viewModel.handleIntent(ProfileContract.Intent.SaveProfile)
                                        },
                                        isLoading = state.isUpdating,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            } else {
                                // View Mode
                                Text(
                                    text = profile.displayName,
                                    style = TriviaTypography.HeadlineMedium,
                                    color = TriviaColors.OnSurface
                                )

                                Text(
                                    text = "@${profile.username}",
                                    style = TriviaTypography.BodyLarge,
                                    color = TriviaColors.OnSurfaceVariant
                                )

                                Text(
                                    text = profile.email,
                                    style = TriviaTypography.BodyMedium,
                                    color = TriviaColors.OnSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

                                AuthSecondaryButton(
                                    text = "Edit Profile",
                                    onClick = {
                                        viewModel.handleIntent(ProfileContract.Intent.StartEditing)
                                    }
                                )
                            }

                            if (state.error != null) {
                                Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))
                                AuthErrorMessage(message = state.error!!)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))

                    // Stats Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = TriviaColors.Surface
                        ),
                        shape = TriviaShapes.Large
                    ) {
                        Column(
                            modifier = Modifier.padding(Dimensions.SpacingLarge)
                        ) {
                            Text(
                                text = "Statistics",
                                style = TriviaTypography.TitleLarge,
                                color = TriviaColors.OnSurface,
                                modifier = Modifier.padding(bottom = Dimensions.SpacingMedium)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    label = "Level",
                                    value = profile.level.toString(),
                                    color = TriviaColors.LevelGold
                                )

                                StatItem(
                                    label = "Score",
                                    value = profile.totalScore.toString(),
                                    color = TriviaColors.XPGreen
                                )

                                StatItem(
                                    label = "Games",
                                    value = profile.gamesPlayed.toString(),
                                    color = TriviaColors.Primary
                                )
                            }

                            Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    label = "Wins",
                                    value = profile.gamesWon.toString(),
                                    color = TriviaColors.Success
                                )

                                StatItem(
                                    label = "Win Rate",
                                    value = "${(profile.winRate * 100).toInt()}%",
                                    color = TriviaColors.Info
                                )

                                StatItem(
                                    label = "Rank",
                                    value = profile.rank,
                                    color = TriviaColors.Secondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))

                    // Badges Card
                    if (profile.badges.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = TriviaColors.Surface
                            ),
                            shape = TriviaShapes.Large
                        ) {
                            Column(
                                modifier = Modifier.padding(Dimensions.SpacingLarge)
                            ) {
                                Text(
                                    text = "Badges",
                                    style = TriviaTypography.TitleLarge,
                                    color = TriviaColors.OnSurface,
                                    modifier = Modifier.padding(bottom = Dimensions.SpacingMedium)
                                )

                                // Show badges in a simple list for now
                                profile.badges.forEach { badge ->
                                    Text(
                                        text = "🏆 $badge",
                                        style = TriviaTypography.BodyMedium,
                                        color = TriviaColors.OnSurface,
                                        modifier = Modifier.padding(vertical = Dimensions.SpacingExtraSmall)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = TriviaTypography.TitleLarge,
            color = color
        )
        Text(
            text = label,
            style = TriviaTypography.LabelMedium,
            color = TriviaColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}