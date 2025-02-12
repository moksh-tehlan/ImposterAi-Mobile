package com.moksh.imposterai.presentation.matchmaking.viewmodel

import com.moksh.imposterai.data.websocket.SocketEvent

sealed interface MatchMakingEvent {
    data class MatchFound(val matchFoundResponse: SocketEvent.MatchFoundResponse) : MatchMakingEvent
    data class Error(val message: String) : MatchMakingEvent
}