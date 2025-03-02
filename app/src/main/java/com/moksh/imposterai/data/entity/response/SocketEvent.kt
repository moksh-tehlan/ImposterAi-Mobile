package com.moksh.imposterai.data.entity.response

import com.moksh.imposterai.data.entity.UserEntity

sealed interface SocketEvent {

    sealed interface GameState : SocketEvent {
        data class MatchFound(
            val matchId: String,
            val currentTyperId: String,
            val opponent: UserEntity
        ) : GameState

        data class TimeUpdate(
            val timeLeft: Int
        ) : GameState
    }

    // Chat Event
    sealed interface ChatEvent : SocketEvent {
        data class MessageReceived(
            val id: String,
            val sender: UserEntity,
            val message: String,
            val currentTyperId: String
        ) : ChatEvent
    }

    // Game lifecycle events
    sealed interface GameLifecycle : SocketEvent {
        data class GameStarted(
            val matchId: String
        ) : GameLifecycle

        data object GameOver : GameLifecycle

        data object PlayerLeft : GameLifecycle
    }

    // Connection events
    sealed interface ConnectionEvent : SocketEvent {
        data object Connected : ConnectionEvent

        data object Disconnected : ConnectionEvent

        data class Error(
            val message: String,
            val status: Int = 500,
            val errors: Map<String, String> = emptyMap()
        ) : ConnectionEvent
    }

}