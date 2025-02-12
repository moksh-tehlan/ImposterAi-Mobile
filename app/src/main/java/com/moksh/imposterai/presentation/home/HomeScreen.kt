package com.moksh.imposterai.presentation.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moksh.imposterai.presentation.common.ObserveAsEvents
import com.moksh.imposterai.presentation.common.PrimaryButton
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme
import com.moksh.imposterai.presentation.home.viewmodel.HomeEvent
import com.moksh.imposterai.presentation.home.viewmodel.HomeState
import com.moksh.imposterai.presentation.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onFindingMatch: () -> Unit,
) {
    val context = LocalContext.current
    ObserveAsEvents(homeViewModel.homeSharedFlow) { event ->
        when (event) {
            is HomeEvent.FindMatchInitiated -> {
                onFindingMatch()
            }

            is HomeEvent.Error -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    HomeScreenView(
        state = homeViewModel.homeState.collectAsStateWithLifecycle().value,
        onStartGame = homeViewModel::findMatch
    )
}

@Composable
private fun HomeScreenView(
    state: HomeState,
    onStartGame: () -> Unit,
) {
    Scaffold { innerPadding ->
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
                isLoading = state.isLoading,
                onClick = onStartGame, text = "Start Game"
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
            onStartGame = {}
        )
    }
}