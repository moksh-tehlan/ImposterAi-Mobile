package com.moksh.imposterai.presentation.profile.viewmodel

sealed interface ProfileActions {
    data class UpdateUsername(val username: String) : ProfileActions
    data class UpdatePassword(val newPassword: String, val confirmPassword: String) : ProfileActions
    data object EnableUsernameEdit : ProfileActions
    data object EnablePasswordEdit : ProfileActions
    data object SaveChanges : ProfileActions
    data object CancelChanges : ProfileActions
    data object OpenPrivacyPolicy : ProfileActions
    data object ShowLogoutConfirmation : ProfileActions
    data object ConfirmLogout : ProfileActions
    data object ShowDeleteAccountConfirmation : ProfileActions
    data object ConfirmDeleteAccount : ProfileActions
    data class ChangeBottomSheet(val bottomSheet: OpenedBottomSheet?) : ProfileActions
}