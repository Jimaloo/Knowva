package com.knowva.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import com.knowva.app.core.di.appModule
import com.knowva.app.core.di.platformModule
import com.knowva.app.presentation.features.home.HomeScreen
import com.knowva.app.presentation.features.home.HomeViewModel
import com.knowva.app.presentation.ui.theme.*

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = TriviaColors.Primary,
            secondary = TriviaColors.Secondary,
            surface = TriviaColors.Surface,
            onSurface = TriviaColors.OnSurface,
            surfaceVariant = TriviaColors.SurfaceVariant,
            onSurfaceVariant = TriviaColors.OnSurfaceVariant,
            error = TriviaColors.Error,
            background = TriviaColors.Surface
        ),
        typography = Typography(
            headlineLarge = TriviaTypography.HeadlineLarge,
            headlineMedium = TriviaTypography.HeadlineMedium,
            titleLarge = TriviaTypography.TitleLarge,
            bodyLarge = TriviaTypography.BodyLarge,
            labelLarge = TriviaTypography.LabelLarge
        ),
        shapes = Shapes(
            small = TriviaShapes.Small,
            medium = TriviaShapes.Medium,
            large = TriviaShapes.Large,
            extraLarge = TriviaShapes.ExtraLarge
        )
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            bottomBar = {
                if (shouldShowBottomBar(currentRoute)) {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                // Pop up to the home destination to avoid building up a large stack
                                popUpTo("home") {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "splash",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("splash") {
                    SplashScreen(
                        onNavigateToAuth = { navController.navigate("auth") },
                        onNavigateToHome = { navController.navigate("home") }
                    )
                }

                composable("auth") {
                    AuthScreen(
                        onNavigateToHome = { navController.navigate("home") },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("home") {
                    val homeViewModel = koinInject<HomeViewModel>()
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToQuickMatch = { navController.navigate("game") },
                        onNavigateToProfile = { navController.navigate("profile") },
                        onNavigateToLeaderboards = { navController.navigate("leaderboards") },
                        onNavigateToAchievements = { navController.navigate("achievements") }
                    )
                }

                composable("game") {
                    GameScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToHome = { navController.navigate("home") }
                    )
                }

                composable("profile") {
                    ProfileScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("leaderboards") {
                    LeaderboardScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("achievements") {
                    AchievementsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("shop") {
                    ShopScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = TriviaColors.Surface,
        contentColor = TriviaColors.OnSurface
    ) {
        val items = listOf(
            BottomNavItem("home", "Home", "🏠"),
            BottomNavItem("game", "Play", "🎮"),
            BottomNavItem("leaderboards", "Leaderboard", "🏆")
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Text(
                        text = item.icon,
                        style = TriviaTypography.TitleMedium
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = TriviaTypography.LabelMedium
                    )
                },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TriviaColors.Primary,
                    selectedTextColor = TriviaColors.Primary,
                    unselectedIconColor = TriviaColors.OnSurfaceVariant,
                    unselectedTextColor = TriviaColors.OnSurfaceVariant,
                    indicatorColor = TriviaColors.Primary.copy(alpha = 0.2f)
                )
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: String
)

private fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return when (currentRoute) {
        "splash", "auth" -> false
        else -> true
    }
}

@Composable
private fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // TODO: Implement splash screen
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        onNavigateToHome() // For now, go directly to home
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        TriviaColors.GradientStart,
                        TriviaColors.GradientEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "KNOWVA",
                style = TriviaTypography.HeadlineLarge,
                color = Color.White
            )
            Spacer(
                modifier = Modifier.height(
                    Dimensions.SpacingMedium
                )
            )
            Text(
                text = "Trivia Challenge",
                style = TriviaTypography.TitleLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun AuthScreen(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // TODO: Implement auth screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.SpacingMedium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Authentication",
            style = TriviaTypography.HeadlineLarge
        )
        Spacer(
            modifier = Modifier.height(
                Dimensions.SpacingLarge
            )
        )
        Button(
            onClick = onNavigateToHome,
            colors = ButtonDefaults.buttonColors(
                containerColor = TriviaColors.Primary
            )
        ) {
            Text("Continue to Home")
        }
    }
}

@Composable
private fun GameScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // TODO: Implement game screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.SpacingMedium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Game Screen",
            style = TriviaTypography.HeadlineLarge
        )
        Spacer(
            modifier = Modifier.height(
                Dimensions.SpacingLarge
            )
        )
        Button(
            onClick = onNavigateBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = TriviaColors.Secondary
            )
        ) {
            Text("Back to Home")
        }
    }
}

@Composable
private fun ProfileScreen(
    onNavigateBack: () -> Unit
) {
    // TODO: Implement profile screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.SpacingMedium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile Screen",
            style = TriviaTypography.HeadlineLarge
        )
        Spacer(
            modifier = Modifier.height(
                Dimensions.SpacingLarge
            )
        )
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
    }
}

@Composable
private fun LeaderboardScreen(
    onNavigateBack: () -> Unit
) {
    // TODO: Implement leaderboard screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.SpacingMedium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Leaderboards",
            style = TriviaTypography.HeadlineLarge
        )
        Spacer(
            modifier = Modifier.height(
                Dimensions.SpacingLarge
            )
        )
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
    }
}

@Composable
private fun AchievementsScreen(
    onNavigateBack: () -> Unit
) {
    // TODO: Implement achievements screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.SpacingMedium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Achievements",
            style = TriviaTypography.HeadlineLarge
        )
        Spacer(
            modifier = Modifier.height(
                Dimensions.SpacingLarge
            )
        )
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
    }
}

@Composable
private fun ShopScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.SpacingMedium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Shop",
            style = TriviaTypography.HeadlineLarge
        )
        Spacer(
            modifier = Modifier.height(
                Dimensions.SpacingLarge
            )
        )
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
    }
}