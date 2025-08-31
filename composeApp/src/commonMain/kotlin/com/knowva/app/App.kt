package com.knowva.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import com.knowva.app.presentation.features.home.HomeScreen
import com.knowva.app.presentation.features.home.HomeViewModel
import com.knowva.app.presentation.features.auth.AuthMainScreen
import com.knowva.app.presentation.features.auth.ProfileScreen
import com.knowva.app.presentation.ui.theme.*
import com.knowva.app.data.repositories.AuthRepository

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val authRepository = koinInject<AuthRepository>()
    var isLoggedIn by remember { mutableStateOf(false) }

    // Check authentication status
    LaunchedEffect(Unit) {
        isLoggedIn = authRepository.isLoggedIn()
    }

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
                startDestination = if (isLoggedIn) "home" else "auth",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("auth") {
                    AuthMainScreen(
                        onNavigateToHome = {
                            isLoggedIn = true
                            navController.navigate("home") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
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
                        onNavigateToAuth = {
                            isLoggedIn = false
                            navController.navigate("auth") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
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
        "auth" -> false
        else -> true
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