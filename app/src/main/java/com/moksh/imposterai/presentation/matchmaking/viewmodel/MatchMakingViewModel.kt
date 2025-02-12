package com.moksh.imposterai.presentation.matchmaking.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moksh.imposterai.data.websocket.SocketEvent
import com.moksh.imposterai.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchMakingViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    init {
        listenSocketEvents()
    }

    private val _matchMakingSharedFlow = MutableSharedFlow<MatchMakingEvent>()
    val matchMakingSharedFlow = _matchMakingSharedFlow.asSharedFlow()

    private fun listenSocketEvents() {
        viewModelScope.launch {
            val flow = gameRepository.socketEvents()

            flow.replayCache.forEach { event ->
                Log.d("Replay Cache", event.toString())
                checkAction(event)
            }

            flow.collectLatest { event ->
                checkAction(event)
            }
        }
    }

    private suspend fun checkAction(event: SocketEvent) {
        when (event) {
            is SocketEvent.MatchFoundResponse -> {
                _matchMakingSharedFlow.emit(MatchMakingEvent.MatchFound(event))
            }

            else -> {}
        }
    }
}