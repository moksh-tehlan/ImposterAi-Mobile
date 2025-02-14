package com.moksh.imposterai.presentation.chat

import android.widget.Toast
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moksh.imposterai.data.entity.UserEntity
import com.moksh.imposterai.data.websocket.SocketEvent
import com.moksh.imposterai.presentation.chat.components.AfterGuessView
import com.moksh.imposterai.presentation.chat.components.ChatScreenAppBar
import com.moksh.imposterai.presentation.chat.components.ChatTextField
import com.moksh.imposterai.presentation.chat.components.GameOverView
import com.moksh.imposterai.presentation.chat.viewmodel.ChatEvent
import com.moksh.imposterai.presentation.chat.viewmodel.ChatState
import com.moksh.imposterai.presentation.chat.viewmodel.ChatViewModel
import com.moksh.imposterai.presentation.common.ObserveAsEvents
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme
import com.moksh.imposterai.presentation.home.viewmodel.HomeEvent
import com.moksh.imposterai.presentation.home.viewmodel.HomeState
import com.moksh.imposterai.presentation.home.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    onGameEnd: () -> Unit,
    onFindingMatch: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ObserveAsEvents(chatViewModel.chatFlow) { chatEvent ->
        when (chatEvent) {
            is ChatEvent.Result -> {
                Toast.makeText(
                    context,
                    "result is ${chatEvent.isCorrectAnswer}",
                    Toast.LENGTH_SHORT
                ).show()

                scope.launch {
                    delay(3000)
                    onGameEnd()
                }
            }
        }
    }

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
    ChatScreenView(
        chatState = chatViewModel.chatState.collectAsStateWithLifecycle().value,
        homeState = homeViewModel.homeState.collectAsStateWithLifecycle().value,
        onChangeChat = chatViewModel::updateCurrentChat,
        onMessageSent = {
            chatViewModel.sendChat()
        },
        result = {
            chatViewModel.submitAnswer(isChattingWithHuman = it)
        },
        onNewGame = { homeViewModel.findMatch() }
    )
}

@Composable
private fun ChatScreenView(
    chatState: ChatState,
    homeState: HomeState,
    onChangeChat: (String) -> Unit,
    onMessageSent: () -> Unit,
    result: (Boolean) -> Unit,
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
            homeState = homeState,
            result = result,
            onNewGame = onNewGame,
        )
    }
}

@Composable
private fun ChatMessagesList(
    modifier: Modifier = Modifier,
    chatState: ChatState,
    homeState: HomeState,
    onChangeChat: (String) -> Unit,
    result: (Boolean) -> Unit,
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
            items = chatState.chats,
            key = { chat -> chat.id } // Assuming each chat has a unique ID
        ) { chat ->
            ChatTextField(
                value = chat.message,
                isMyChat = chat.sender.id == chatState.currentPlayer?.id
            )
        }

        if (!chatState.gameOver) {
            item(key = "input") {
                val isCurrentPlayer = chatState.currentTurn == chatState.currentPlayer?.id
                ChatTextField(
                    isEnabled = isCurrentPlayer,
                    isMyChat = isCurrentPlayer,
                    value = if (isCurrentPlayer) chatState.currentMessage else "",
                    onMessageSent = if (isCurrentPlayer) onMessageSent else null,
                    onValueChange = if (isCurrentPlayer) onChangeChat else null,
                    isLoading = !isCurrentPlayer
                )
            }
        }

        if (chatState.gameOver && chatState.playerWon == null) {
            item(key = "gameover") {
                GameOverView(
                    resultSubmitted = chatState.isResultSubmitted,
                    aiButtonLoading = chatState.isAiButtonLoading,
                    humanButtonLoading = chatState.isHumanButtonLoading,
                    onAiButtonClick = { result(false) },
                    onHumanButtonClick = { result(true) }
                )
            }
        }
        if (chatState.playerWon != null) {
            item(key = "afterguess") {
                AfterGuessView(
                    isBot = chatState.isOpponentAnAi ?: false,
                    playerWon = chatState.playerWon,
                    onNewGame = onNewGame,
                    isButtonLoading = homeState.isLoading,
                )
            }
        }
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(chatState.chats.size, chatState.gameOver) {
        listState.animateScrollToItem(chatState.chats.size)
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
                gameOver = true,
                chats = List(
                    size = 4,
                    init = {
                        SocketEvent.Chat(
                            message = "Hello there",
                            sender = UserEntity(
                                id = if (it % 2 == 0) "abc" else "xyx",
                                username = "moksh"
                            ),
                            id = "abc$it",
                            currentTyperId = ""
                        )
                    }
                )
            ), homeState = HomeState(),
            onNewGame = {}
        )
    }
}