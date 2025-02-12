package com.moksh.imposterai.presentation.chat.viewmodel

sealed interface ChatEvent {
    data class Result(val isCorrectAnswer: Boolean) : ChatEvent
}