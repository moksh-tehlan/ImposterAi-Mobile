package com.moksh.imposterai.presentation.game_viewmodel

sealed interface GameEvent {
    data object NavigateToMatchMaking : GameEvent
    data object NavigateToHomeScreen : GameEvent
    data object MatchFound : GameEvent
    data class Error(val text: String) : GameEvent
}