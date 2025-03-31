package com.moksh.imposterai.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moksh.imposterai.data.local.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState = _profileState.asStateFlow()
        .onStart { loadUserDetails() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ProfileState()
        )

    // For long-running operations and API calls that don't return immediately
    private val _profileActions = MutableSharedFlow<ProfileActions>()
    val profileActions = _profileActions.asSharedFlow()

    // For one-time events like navigation, messages, etc.
    private val _profileEvents = MutableSharedFlow<ProfileEvent>()
    val profileEvents = _profileEvents.asSharedFlow()

    /**
     * Central function to handle all profile actions
     */
    fun handleAction(action: ProfileActions) {
        when (action) {
            is ProfileActions.UpdateUsername -> changeUsername(action.username)
            is ProfileActions.EnableUsernameEdit -> enableUsernameChange()
            is ProfileActions.EnablePasswordEdit -> enablePasswordChange()
            is ProfileActions.SaveChanges -> onSaveDetails()
            is ProfileActions.CancelChanges -> cancelChanges()
            is ProfileActions.ShowLogoutConfirmation -> onLogoutTextClick()
            is ProfileActions.ShowDeleteAccountConfirmation -> onDeleteAccountTextClick()
            is ProfileActions.ChangeBottomSheet -> changeBottomSheet(action.bottomSheet)
            is ProfileActions.UpdatePassword -> {
                changePassword(action.newPassword)
                changeConfirmPassword(action.confirmPassword)
            }

            is ProfileActions.OpenPrivacyPolicy -> {
                viewModelScope.launch {
                    _profileEvents.emit(ProfileEvent.OpenUrl("https://imposterai.com/privacy-policy"))
                }
            }

            is ProfileActions.ConfirmLogout -> {
                viewModelScope.launch {
                    // Handle logout API call here
                    onLogout()
                    // Emit navigation event
                    _profileEvents.emit(ProfileEvent.NavigateToLogin)
                }
            }

            is ProfileActions.ConfirmDeleteAccount -> {
                viewModelScope.launch {
                    // Handle delete account API call here
                    onDeleteAccount()
                    // Show success message

                    // Navigate to login screen
                    _profileEvents.emit(ProfileEvent.NavigateToLogin)
                }
            }
        }
    }

    private fun loadUserDetails() {
        // Here you would normally fetch user details from repository
        // For now, we'll use hardcoded values
        val userEntity = sharedPreferencesManager.getUser()
        userEntity?.let { user ->
            _profileState.update {
                it.copy(
                    username = user.username,
                    password = "password1234"
                )
            }
        }
    }

    fun enableUsernameChange() {
        _profileState.update {
            it.copy(
                changeUsernameEnabled = true,
                newUsername = it.username
            )
        }
    }

    fun enablePasswordChange() {
        _profileState.update {
            it.copy(
                currentBottomSheet = OpenedBottomSheet.ChangePassword,
                newPassword = "",
                confirmPassword = ""
            )
        }
    }

    fun changeBottomSheet(openedBottomSheet: OpenedBottomSheet? = null) {
        _profileState.update {
            it.copy(
                currentBottomSheet = openedBottomSheet
            )
        }
    }

    fun changeUsername(username: String) {
        _profileState.update {
            it.copy(
                newUsername = username,
                isButtonEnabled = username.isNotEmpty() && username != it.username
            )
        }
    }

    fun changePassword(password: String) {
        _profileState.update {
            it.copy(
                newPassword = password,
                isButtonEnabled = password.isNotEmpty() &&
                        password == it.confirmPassword
            )
        }
    }

    fun changeConfirmPassword(password: String) {
        _profileState.update {
            it.copy(
                confirmPassword = password,
                isButtonEnabled = password.isNotEmpty() &&
                        password == it.newPassword
            )
        }
    }

    private fun cancelChanges() {
        _profileState.update {
            it.copy(
                changeUsernameEnabled = false,
                newUsername = it.username,
                newPassword = "",
                confirmPassword = "",
                currentBottomSheet = null,
                isButtonEnabled = false
            )
        }
    }

    fun onSaveDetails() {
        viewModelScope.launch {
            _profileState.update {
                it.copy(isButtonLoading = true)
            }

            // Determine which update to perform
            if (_profileState.value.changeUsernameEnabled &&
                _profileState.value.newUsername != _profileState.value.username
            ) {
                // Update username
                updateUsername()
            } else if (_profileState.value.currentBottomSheet == OpenedBottomSheet.ChangePassword &&
                _profileState.value.newPassword.isNotEmpty() &&
                _profileState.value.newPassword == _profileState.value.confirmPassword
            ) {
                // Update password
                updatePassword()
            }

            _profileState.update {
                it.copy(
                    isButtonLoading = false,
                    isButtonEnabled = false,
                    changeUsernameEnabled = false,
                    username = if (it.changeUsernameEnabled) it.newUsername else it.username
                )
            }
        }
    }

    fun onLogoutTextClick() {
        _profileState.update {
            it.copy(
                currentBottomSheet = OpenedBottomSheet.LogoutConfirmation
            )
        }
    }

    fun onLogout() {
        sharedPreferencesManager.clearAll();
        // do logout things
    }

    fun onDeleteAccountTextClick() {
        _profileState.update {
            it.copy(
                currentBottomSheet = OpenedBottomSheet.DeleteAccountConfirmation
            )
        }
    }

    fun onDeleteAccount() {
        viewModelScope.launch {
            sharedPreferencesManager.clearAll()
            _profileEvents.emit(ProfileEvent.ShowDeleteAccountSuccess)
        }
    }

    private suspend fun updateUsername() {
        try {
            // Here you would update the username in your repository
            // For now, we'll just emit the action
            _profileActions.emit(ProfileActions.UpdateUsername(_profileState.value.newUsername))

            // Update local state once "API call" is successful
            _profileState.update {
                it.copy(
                    username = it.newUsername,
                    changeUsernameEnabled = false
                )
            }

            // Show success message
            _profileEvents.emit(ProfileEvent.ShowMessage("Username updated successfully"))
        } catch (e: Exception) {
            _profileEvents.emit(ProfileEvent.ShowError("Failed to update username: ${e.message}"))
        }
    }

    private suspend fun updatePassword() {
        try {
            // Here you would update the password in your repository
            // For now, we'll just emit the action
            _profileActions.emit(
                ProfileActions.UpdatePassword(
                    _profileState.value.newPassword,
                    _profileState.value.confirmPassword
                )
            )

            // Update local state once "API call" is successful
            _profileState.update {
                it.copy(
                    password = it.newPassword,
                    newPassword = "",
                    confirmPassword = "",
                    currentBottomSheet = null
                )
            }

            // Show success message
            _profileEvents.emit(ProfileEvent.ShowMessage("Password updated successfully"))
        } catch (e: Exception) {
            _profileEvents.emit(ProfileEvent.ShowError("Failed to update password: ${e.message}"))
        }
    }
}