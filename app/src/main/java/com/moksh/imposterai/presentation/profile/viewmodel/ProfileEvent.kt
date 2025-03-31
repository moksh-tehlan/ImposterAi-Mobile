package com.moksh.imposterai.presentation.profile.viewmodel

sealed interface ProfileEvent {
    data object NavigateBack : ProfileEvent
    data class OpenUrl(val url: String) : ProfileEvent
    data object NavigateToLogin : ProfileEvent // After logout
    data object ShowDeleteAccountSuccess : ProfileEvent
    data class ShowMessage(val message: String) : ProfileEvent
    data class ShowError(val error: String) : ProfileEvent
}