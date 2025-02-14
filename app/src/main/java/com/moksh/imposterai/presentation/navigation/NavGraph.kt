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
import com.moksh.imposterai.presentation.auth.LoginScreen
import com.moksh.imposterai.presentation.auth.SingUpScreen
import com.moksh.imposterai.presentation.chat.ChatScreen
import com.moksh.imposterai.presentation.core.theme.Black
import com.moksh.imposterai.presentation.home.HomeScreen
import com.moksh.imposterai.presentation.matchmaking.MatchMakingScreen

@Composable
fun NavGraph(
    sharedPref: SharedPreferencesManager,
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
                    navController.navigate(Routes.Home, builder = {
                        popUpTo(Graphs.AuthGraph) {
                            inclusive = true
                        }
                    })
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
            HomeScreen(
                onFindingMatch = { navController.navigate(Routes.MatchMaking) }
            )
        }
        composable<Routes.MatchMaking> {
            MatchMakingScreen(
                onMatchFound = {
                    navController.popBackStack()
                    navController.navigate(
                        Routes.Chat(
                            matchId = it.matchId,
                            currentTyperId = it.currentTyperId,
                        )
                    )
                },
            )
        }

        composable<Routes.Chat> {
            ChatScreen(
                onGameEnd = { navController.popBackStack() },
                onFindingMatch = {
                    navController.popBackStack()
                    navController.navigate(Routes.MatchMaking)
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