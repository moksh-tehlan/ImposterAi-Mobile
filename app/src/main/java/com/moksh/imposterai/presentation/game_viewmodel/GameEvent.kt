package com.moksh.imposterai.presentation.game_viewmodel

import com.moksh.imposterai.data.entity.response.SocketEvent

sealed interface GameEvent {
    data object NavigateToMatchMaking : GameEvent
    data object NavigateToHomeScreen : GameEvent
    data object MatchFound : GameEvent
    data class Error(val error: SocketEvent.ConnectionEvent.Error) : GameEvent
}