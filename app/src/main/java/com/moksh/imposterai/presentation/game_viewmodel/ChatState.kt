package com.moksh.imposterai.presentation.game_viewmodel

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val currentDraftedMessage: String = "",
    val userGuess: OpponentType? = null,
    val timeLeft: Int = 120,
)

data class ChatMessage(
    val id: String,
    val message: String,
    val senderId: String,
)