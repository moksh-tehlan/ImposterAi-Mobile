package com.moksh.imposterai.presentation.matchmaking

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moksh.imposterai.data.websocket.SocketEvent
import com.moksh.imposterai.presentation.common.ObserveAsEvents
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme
import com.moksh.imposterai.presentation.matchmaking.viewmodel.MatchMakingEvent
import com.moksh.imposterai.presentation.matchmaking.viewmodel.MatchMakingViewModel

@Composable
fun MatchMakingScreen(
    onMatchFound: (opponent: SocketEvent.MatchFoundResponse) -> Unit,
    viewModel: MatchMakingViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    ObserveAsEvents(viewModel.matchMakingSharedFlow) { event ->
        when (event) {
            is MatchMakingEvent.MatchFound -> {
                onMatchFound(event.matchFoundResponse)
            }

            is MatchMakingEvent.Error -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
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