package com.moksh.imposterai.presentation.chat.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moksh.imposterai.data.entity.request.GameResultRequest
import com.moksh.imposterai.data.local.SharedPreferencesManager
import com.moksh.imposterai.data.websocket.SocketEvent
import com.moksh.imposterai.domain.repository.GameRepository
import com.moksh.imposterai.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val savedStateHandle: SavedStateHandle,
    private val sharedPreferencesManager: SharedPreferencesManager,
) : ViewModel() {

    private val _chatFlow = MutableSharedFlow<ChatEvent>()
    val chatFlow = _chatFlow.asSharedFlow()
    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()
        .onStart {
            listenSocketEvents()
            val matchId =
                savedStateHandle.get<String>("matchId")

            val currentTyperId = savedStateHandle.get<String>("currentTyperId")
            val currentPlayer = sharedPreferencesManager.getUser()

            if (matchId != null && currentPlayer != null && currentTyperId != null) {
                _chatState.update {
                    it.copy(
                        currentTurn = currentTyperId,
                        matchId = matchId,
                        currentPlayer = currentPlayer
                    )
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ChatState()
        )

    private fun listenSocketEvents() {
        viewModelScope.launch {
            gameRepository.socketEvents().collectLatest { event ->
                when (event) {
                    is SocketEvent.TimeLeft -> {
                        updateTimeLeft(event.timeLeft)
                    }

                    is SocketEvent.Chat -> {
                        handleChat(event)
                    }

                    is SocketEvent.TurnChange -> {
                        updateCurrentTurnPlayer(event.userId)
                    }

                    is SocketEvent.GameOver -> {
                        _chatState.update {
                            it.copy(gameOver = true)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun updateTimeLeft(timeLeft: Int) {
        _chatState.update {
            it.copy(timeLeft = timeLeft)
        }
    }

    private fun updateCurrentTurnPlayer(id: String) {
        _chatState.update {
            it.copy(currentTurn = id)
        }
    }

    private fun handleChat(chat: SocketEvent.Chat) {
        _chatState.update {
            it.copy(chats = it.chats + chat, currentTurn = chat.currentTyperId)
        }
    }

    fun updateCurrentChat(message: String) {
        _chatState.update {
            it.copy(currentMessage = message)
        }
    }

    fun sendChat() {
        viewModelScope.launch {
            val currentPlayer = _chatState.value.currentPlayer ?: return@launch
            val chat = SocketEvent.Chat(
                id = UUID.randomUUID().toString(),
                sender = currentPlayer,
                message = _chatState.value.currentMessage,
                currentTyperId = ""
            )
            handleChat(chat)
            when (val result = gameRepository.sendMessage(_chatState.value.currentMessage)) {
                is Result.Success -> {
                    _chatState.update {
                        it.copy(currentMessage = "")
                    }
                }

                is Result.Error -> {
                    Log.d("ChatViewModel", result.error.name)
                }
            }
        }
    }

    fun submitAnswer(isChattingWithHuman: Boolean) {
        val gameResultRequest = GameResultRequest(
            matchId = _chatState.value.matchId,
            isOpponentAHuman = isChattingWithHuman,
        )
        Log.d("Opponent a human: ",isChattingWithHuman.toString())
        _chatState.update {
            it.copy(
                isHumanButtonLoading = isChattingWithHuman,
                isResultSubmitted = true,
                isAiButtonLoading = !isChattingWithHuman
            )
        }
        viewModelScope.launch {
            val result = gameRepository.checkResult(gameResultRequest)

            when (result) {
                is Result.Success -> {
                    _chatFlow.emit(ChatEvent.Result(isCorrectAnswer = result.data))
                }

                is Result.Error -> {
                    _chatFlow.emit(ChatEvent.Result(isCorrectAnswer = false))
                }
            }

            _chatState.update {
                it.copy(
                    isHumanButtonLoading = false,
                    isAiButtonLoading = false
                )
            }
        }
    }
}