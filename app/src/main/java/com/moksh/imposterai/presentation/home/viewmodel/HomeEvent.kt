package com.moksh.imposterai.presentation.home.viewmodel

sealed interface HomeEvent {
    data object FindMatchInitiated : HomeEvent
    data class Error(val message: String) : HomeEvent
}