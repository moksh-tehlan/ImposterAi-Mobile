package com.moksh.imposterai.presentation.chat.viewmodel

import com.moksh.imposterai.data.entity.UserEntity
import com.moksh.imposterai.data.websocket.SocketEvent

data class ChatState(
    val timeLeft: Int = 120,
    val matchId:String = "",
    val currentTurn: String = "",
    val currentPlayer: UserEntity? = null,
    val currentMessage: String = "",
    val gameOver: Boolean = false,
    val chats: List<SocketEvent.Chat> = emptyList(),
    val isResultSubmitted: Boolean = false,
    val isHumanButtonLoading: Boolean = false,
    val isAiButtonLoading: Boolean = false,
)