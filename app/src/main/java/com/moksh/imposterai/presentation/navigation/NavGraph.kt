package com.moksh.imposterai.presentation.navigation

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.moksh.imposterai.data.local.SharedPreferencesManager
import com.moksh.imposterai.data.local.TokenManager
import com.moksh.imposterai.presentation.auth.LoginScreen
import com.moksh.imposterai.presentation.auth.SingUpScreen
import com.moksh.imposterai.presentation.chat.ChatScreen
import com.moksh.imposterai.presentation.core.theme.Black
import com.moksh.imposterai.presentation.game_viewmodel.GameViewModel
import com.moksh.imposterai.presentation.home.HomeScreen
import com.moksh.imposterai.presentation.matchmaking.MatchMakingScreen
import com.moksh.imposterai.presentation.profile.ProfilePage

@Composable
fun NavGraph(
    sharedPref: SharedPreferencesManager,
    tokenMananger: TokenManager,
) {
    val startDestination = if (sharedPref.isLoggedIn) Graphs.HomeGraph else Graphs.AuthGraph
    Log.d("StartDestination", startDestination.toString())
    val navController = rememberNavController();
    NavHost(
        modifier = Modifier.background(Black),
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                ),
                initialOffsetX = { fullWidth -> fullWidth }
            )
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                ),
                targetOffsetX = { fullWidth -> -fullWidth }
            ) + fadeOut(animationSpec = tween(500))
        },
        popEnterTransition = {
            slideInHorizontally(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                ),
                initialOffsetX = { fullWidth -> -fullWidth }
            ) + fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutHorizontally(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                ),
                targetOffsetX = { fullWidth -> fullWidth }
            )
        }
    ) {
        authGraph(navController)
        homeGraph(navController)
    }
    LaunchedEffect(key1 = Unit) {
        tokenMananger.navigationEvent.collect { route ->
            when (route) {
                Routes.Login -> {
                    navController.navigate(route) {
                        popUpTo(0) { inclusive = true }
                    }
                }

                else -> {
                    navController.navigate(route)
                }
            }
        }
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation<Graphs.AuthGraph>(
        startDestination = Routes.Login
    ) {
        composable<Routes.Login> {
            LoginScreen(
                onSignUpClick = { navController.navigate(Routes.Signup) },
                onLoginSuccessful = {
                    navController.navigate(Routes.Home, builder = {
                        popUpTo(Graphs.AuthGraph) {
                            inclusive = true
                        }
                    })
                }
            )
        }
        composable<Routes.Signup> {
            SingUpScreen(
                onLoginClick = { navController.popBackStack() },
                onSignUpSuccessful = {
                    navController.popBackStack()
                }
            )
        }
    }
}

private fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    navigation<Graphs.HomeGraph>(
        startDestination = Routes.Home
    ) {
        composable<Routes.Home> { entry ->
            val gameViewModel = entry.sharedViewModel<GameViewModel>(navController)
            HomeScreen(
                gameViewModel = gameViewModel,
                onNavigateToMatchmakingScreen = { navController.navigate(Routes.MatchMaking) },
                onNavigateToProfileScreen = { navController.navigate(Routes.Profile) }
            )
        }
        composable<Routes.MatchMaking> { entry ->
            val gameViewModel = entry.sharedViewModel<GameViewModel>(navController)
            MatchMakingScreen(
                gameViewModel = gameViewModel,
                onMatchFound = {
                    navController.popBackStack()
                    navController.navigate(Routes.Chat)
                },
                onPopBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Routes.Chat> { entry ->
            val gameViewModel = entry.sharedViewModel<GameViewModel>(navController)
            ChatScreen(
                gameViewModel = gameViewModel,
                onNavigateToHomeScreen = { navController.popBackStack() },
                onNavigateToMatchMaking = {
                    navController.popBackStack()
                    navController.navigate(Routes.MatchMaking)
                }
            )
        }

        composable<Routes.Profile> {
            ProfilePage(
                onPopBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.Login, builder = {
                        popUpTo(Graphs.HomeGraph) {
                            inclusive = true
                        }
                    })
                },
                onDeleteAccount = {
                    navController.navigate(Routes.Login, builder = {
                        popUpTo(Graphs.HomeGraph) {
                            inclusive = true
                        }
                    })
                }
            )
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}