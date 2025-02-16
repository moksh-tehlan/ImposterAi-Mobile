package com.moksh.imposterai.presentation.matchmaking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moksh.imposterai.presentation.common.ObserveAsEvents
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme
import com.moksh.imposterai.presentation.game_viewmodel.GameEvent
import com.moksh.imposterai.presentation.game_viewmodel.GameViewModel

@Composable
fun MatchMakingScreen(
    onMatchFound: () -> Unit,
    gameViewModel: GameViewModel,
) {
    ObserveAsEvents(gameViewModel.gameEventFlow) { event ->
        when (event) {
            is GameEvent.Error -> {}
            is GameEvent.MatchFound -> onMatchFound()
            else -> {}
        }
    }
    MatchMakingScreenView()
}

@Composable
private fun MatchMakingScreenView() {
    Scaffold { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                "Finding match...", style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    }
}

@Composable
@Preview
private fun MatchMakingScreenPreview() {
    ImposterAiTheme {
        MatchMakingScreenView()
    }
}