package com.moksh.imposterai.presentation.profile.viewmodel

data class ProfileState(
    val username: String = "",
    val password: String = "",
    val changeUsernameEnabled: Boolean = false,
    val newUsername: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentBottomSheet: OpenedBottomSheet? = null,
    val isButtonLoading: Boolean = false,
    val isButtonEnabled: Boolean = false,
)

sealed interface OpenedBottomSheet {
    data object ChangePassword : OpenedBottomSheet
    data object LogoutConfirmation : OpenedBottomSheet
    data object DeleteAccountConfirmation : OpenedBottomSheet
}