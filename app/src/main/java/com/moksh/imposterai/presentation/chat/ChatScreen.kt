package com.moksh.imposterai.presentation.chat

import android.health.connect.datatypes.units.Length
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moksh.imposterai.presentation.chat.components.AfterGuessView
import com.moksh.imposterai.presentation.chat.components.ChatScreenAppBar
import com.moksh.imposterai.presentation.chat.components.ChatTextField
import com.moksh.imposterai.presentation.chat.components.GameOverView
import com.moksh.imposterai.presentation.chat.components.PlayerLeftView
import com.moksh.imposterai.presentation.common.ObserveAsEvents
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme
import com.moksh.imposterai.presentation.game_viewmodel.ChatState
import com.moksh.imposterai.presentation.game_viewmodel.GameEvent
import com.moksh.imposterai.presentation.game_viewmodel.GamePhase
import com.moksh.imposterai.presentation.game_viewmodel.GamePlayState
import com.moksh.imposterai.presentation.game_viewmodel.GameViewModel
import com.moksh.imposterai.presentation.game_viewmodel.MatchState
import com.moksh.imposterai.presentation.game_viewmodel.OpponentType

@Composable
fun ChatScreen(
    gameViewModel: GameViewModel,
    onNavigateToHomeScreen: () -> Unit,
    onNavigateToMatchMaking: () -> Unit,
) {

    BackHandler {
        gameViewModel.handleBackPress()
    }
    val context = LocalContext.current
    ObserveAsEvents(gameViewModel.gameEventFlow) { event ->
        when (event) {
            is GameEvent.NavigateToMatchMaking -> onNavigateToMatchMaking()
            is GameEvent.NavigateToHomeScreen -> onNavigateToHomeScreen()
            is GameEvent.Error -> {
                Toast.makeText(context,event.error.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    ChatScreenView(
        chatState = gameViewModel.chatState.collectAsStateWithLifecycle().value,
        gamePlayState = gameViewModel.gamePlayState.collectAsStateWithLifecycle().value,
        onChangeChat = gameViewModel::updateCurrentDraftedMessage,
        onMessageSent = gameViewModel::sendMessage,
        result = gameViewModel::guessAnswer,
        onNewGame = gameViewModel::findMatch,
        matchState = gameViewModel.matchState.collectAsStateWithLifecycle().value,
    )
}

@Composable
private fun ChatScreenView(
    chatState: ChatState,
    gamePlayState: GamePlayState,
    matchState: MatchState,
    onChangeChat: (String) -> Unit,
    onMessageSent: () -> Unit,
    result: (OpponentType) -> Unit,
    onNewGame: () -> Unit
) {
    Scaffold(
        topBar = {
            ChatScreenAppBar(
                modifier = Modifier.padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                ), timeLeft = chatState.timeLeft
            )
        }
    ) { innerPadding ->
        ChatMessagesList(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            chatState = chatState,
            onChangeChat = onChangeChat,
            onMessageSent = onMessageSent,
            gameState = gamePlayState,
            result = result,
            matchState = matchState,
            onNewGame = onNewGame,
        )
    }
}

@Composable
private fun ChatMessagesList(
    modifier: Modifier = Modifier,
    chatState: ChatState,
    gameState: GamePlayState,
    matchState: MatchState,
    onChangeChat: (String) -> Unit,
    result: (OpponentType) -> Unit,
    onMessageSent: () -> Unit,
    onNewGame: () -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items(
            items = chatState.messages,
            key = { chat -> chat.id } // Assuming each chat has a unique ID
        ) { chat ->
            ChatTextField(
                value = chat.message,
                isMyChat = chat.senderId == matchState.currentPlayer?.id
            )
        }

        if (gameState.gamePhase == GamePhase.InProgress) {
            item(key = "input") {
                ChatTextField(
                    isEnabled = matchState.isMyTurn,
                    isMyChat = matchState.isMyTurn,
                    value = if (matchState.isMyTurn) chatState.currentDraftedMessage else "",
                    onMessageSent = if (matchState.isMyTurn) onMessageSent else null,
                    onValueChange = if (matchState.isMyTurn) onChangeChat else null,
                    isLoading = !matchState.isMyTurn
                )
            }
        }

        if (gameState.gamePhase == GamePhase.GameOver) {
            item(key = "gameover") {
                GameOverView(
                    aiButtonLoading = chatState.userGuess == OpponentType.AIBot,
                    humanButtonLoading = chatState.userGuess == OpponentType.Human,
                    onAiButtonClick = { result(OpponentType.AIBot) },
                    onHumanButtonClick = { result(OpponentType.Human) }
                )
            }
        }
        if (gameState.gamePhase is GamePhase.Guessed) {
            val playerWinningStatus = gameState.gamePhase.isCorrectGuess
            val opponentType = gameState.gamePhase.opponentType
            item(key = "afterguess") {
                AfterGuessView(
                    opponentType = opponentType,
                    playerWon = playerWinningStatus,
                    onNewGame = onNewGame,
                    isButtonLoading = false,
                )
            }
        }

        if (gameState.gamePhase is GamePhase.PlayerLeft) {
            item(key = "playerleft") {
                PlayerLeftView(
                    isButtonLoading = false,
                    onNewGame = onNewGame
                )
            }
        }
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(chatState.messages.size, gameState.gamePhase) {
        listState.animateScrollToItem(chatState.messages.size)
    }
}

@Composable
@Preview
private fun ChatScreenPreview() {
    ImposterAiTheme {
        ChatScreenView(
            onChangeChat = {},
            onMessageSent = {},
            result = {},
            chatState = ChatState(
                timeLeft = 120,
            ),
            gamePlayState = GamePlayState(),
            matchState = MatchState(),
            onNewGame = {}
        )
    }
}