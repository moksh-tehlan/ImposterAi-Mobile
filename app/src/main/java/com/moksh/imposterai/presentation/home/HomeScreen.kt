package com.moksh.imposterai.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moksh.imposterai.presentation.common.ObserveAsEvents
import com.moksh.imposterai.presentation.common.PrimaryButton
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme
import com.moksh.imposterai.presentation.game_viewmodel.GameEvent
import com.moksh.imposterai.presentation.game_viewmodel.GameViewModel
import com.moksh.imposterai.presentation.home.components.ProfileLogo
import com.moksh.imposterai.presentation.home.viewmodel.HomeState
import com.moksh.imposterai.presentation.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    gameViewModel: GameViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(),
    onNavigateToMatchmakingScreen: () -> Unit,
    onNavigateToProfileScreen: () -> Unit,
) {
    ObserveAsEvents(gameViewModel.gameEventFlow) { event ->
        when (event) {
            is GameEvent.NavigateToMatchMaking -> {
                homeViewModel.switchButtonState()
                onNavigateToMatchmakingScreen()
            }

            is GameEvent.Error -> homeViewModel.switchButtonState()
            else -> {}
        }
    }
    HomeScreenView(
        state = homeViewModel.homeState.collectAsStateWithLifecycle().value,
        onStartGame = {
            homeViewModel.switchButtonState()
            gameViewModel.findMatch()
        },
        onProfileClick = onNavigateToProfileScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenView(
    state: HomeState,
    onStartGame: () -> Unit,
    onProfileClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(end = 25.dp),
                title = {},
                actions = {
                    ProfileLogo(onClick = onProfileClick)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Human or not?",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Chat with someone for two minutes, and try to figure out if it was a fellow human or an AI bot.\n" +
                        "\n" +
                        "Think you can tell the difference?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSecondary
                ),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                isLoading = state.buttonLoading,
                onClick = {
                    onStartGame()
                }, text = "Start Game"
            )
        }
    }
}


@Composable
@Preview
private fun HomeScreenPreview() {
    ImposterAiTheme {
        HomeScreenView(
            state = HomeState(),
            onStartGame = {},
            onProfileClick = {},
        )
    }
}