package com.moksh.imposterai.presentation.game_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moksh.imposterai.data.entity.UserEntity
import com.moksh.imposterai.data.entity.request.GameResultRequest
import com.moksh.imposterai.data.entity.response.SocketEvent
import com.moksh.imposterai.data.local.SharedPreferencesManager
import com.moksh.imposterai.domain.repository.GameRepository
import com.moksh.imposterai.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    sharedPreferencesManager: SharedPreferencesManager,
) : ViewModel() {

    private val _gameEventFlow = MutableSharedFlow<GameEvent>()
    val gameEventFlow = _gameEventFlow.asSharedFlow()

    private val _gamePlayState = MutableStateFlow(GamePlayState())
    val gamePlayState = _gamePlayState.asStateFlow()

    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    private val _matchState = MutableStateFlow(MatchState())
    val matchState = _matchState.asStateFlow()

    private var currentPlayer: UserEntity;

    init {
        listenWebsocketEvent()
        currentPlayer = sharedPreferencesManager.getUser()!!
    }

    private fun listenWebsocketEvent() {
        viewModelScope.launch {
            gameRepository.socketEvents().collect { event ->
                when (event) {
                    is SocketEvent.GameState -> handleGameState(event)
                    is SocketEvent.ChatEvent -> handleChat(event)
                    is SocketEvent.GameLifecycle -> handleGameLifecycle(event)
                    is SocketEvent.ConnectionEvent -> handleConnectionEvent(event)
                }
            }
        }
    }

    private fun handleGameState(event: SocketEvent.GameState) {
        when (event) {
            is SocketEvent.GameState.MatchFound -> {
                _matchState.update {
                    it.copy(
                        matchId = event.matchId,
                        opponent = event.opponent,
                        currentPlayer = currentPlayer
                    )
                }
                _gamePlayState.update {
                    it.copy(gamePhase = GamePhase.InProgress)
                }
                viewModelScope.launch { _gameEventFlow.emit(GameEvent.MatchFound) }
            }

            is SocketEvent.GameState.TimeUpdate -> {
                _chatState.update {
                    it.copy(timeLeft = event.timeLeft)
                }
            }
        }
    }

    private fun handleChat(event: SocketEvent.ChatEvent) {
        when (event) {
            is SocketEvent.ChatEvent.MessageReceived -> {
                val chatMessage =
                    ChatMessage(id = event.id, senderId = event.sender.id, message = event.message)
                _chatState.update {
                    it.copy(messages = it.messages + chatMessage)
                }
                _matchState.update {
                    it.copy(currentTyper = event.currentTyperId)
                }
            }
        }
    }

    private fun handleGameLifecycle(event: SocketEvent.GameLifecycle) {
        when (event) {
            is SocketEvent.GameLifecycle.GameOver -> {
                _gamePlayState.update {
                    it.copy(gamePhase = GamePhase.GameOver)
                }
            }

            is SocketEvent.GameLifecycle.GameStarted -> {
                _gamePlayState.update {
                    it.copy(gamePhase = GamePhase.InProgress)
                }
            }

            is SocketEvent.GameLifecycle.PlayerLeft -> {
                _gamePlayState.update {
                    it.copy(gamePhase = GamePhase.PlayerLeft)
                }
            }
        }
    }

    private fun handleConnectionEvent(event: SocketEvent.ConnectionEvent) {
        when (event) {
            is SocketEvent.ConnectionEvent.Connected -> {}
            is SocketEvent.ConnectionEvent.Disconnected -> {}
            is SocketEvent.ConnectionEvent.Error -> {}
        }
    }

    fun findMatch() {
        resetAllState()
        viewModelScope.launch {
            val result = gameRepository.findMatch()
            when (result) {
                is Result.Success -> {
                    _gamePlayState.update { it.copy(gamePhase = GamePhase.MatchMaking) }
                    _gameEventFlow.emit(GameEvent.NavigateToMatchMaking)
                }

                is Result.Error -> {

                }
            }
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            val message = _chatState.value.currentDraftedMessage
            _chatState.update { it.copy(currentDraftedMessage = "") }
            _matchState.update { it.copy(currentTyper = "") }

            val result = gameRepository.sendMessage(message)

            when (result) {
                is Result.Success -> {
                    val chatMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        senderId = currentPlayer.id,
                        message = message,
                    )
                    _chatState.update {
                        it.copy(messages = it.messages + chatMessage)
                    }
                }

                is Result.Error -> {

                }
            }
        }
    }

    fun updateCurrentDraftedMessage(message: String) {
        _chatState.update {
            it.copy(
                currentDraftedMessage = message
            )
        }
    }

    fun guessAnswer(opponentType: OpponentType) {
        _chatState.update { it.copy(userGuess = opponentType) }
        viewModelScope.launch {
            val gameResult = GameResultRequest(
                isOpponentAHuman = opponentType == OpponentType.Human,
                matchId = _matchState.value.matchId
            )
            when (val result = gameRepository.checkResult(gameResult)) {
                is Result.Success -> {
                    _gamePlayState.update {
                        it.copy(
                            gamePhase = GamePhase.Guessed(
                                isCorrectGuess = result.data,
                                opponentType = if (result.data == (opponentType == OpponentType.Human)) OpponentType.Human else OpponentType.AIBot
                            )
                        )
                    }
                }

                is Result.Error -> {}
            }
        }
    }

    private fun resetAllState() {
        _matchState.value = MatchState()
        _chatState.value = ChatState()
        _gamePlayState.update { it.copy(gamePhase = GamePhase.NotStarted) }
    }

}