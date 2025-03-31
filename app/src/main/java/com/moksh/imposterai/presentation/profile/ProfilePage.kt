package com.moksh.imposterai.presentation.profile

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moksh.imposterai.presentation.common.ObserveAsEvents
import com.moksh.imposterai.presentation.common.PrimaryButton
import com.moksh.imposterai.presentation.common.PrimaryTextField
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme
import com.moksh.imposterai.presentation.core.theme.White
import com.moksh.imposterai.presentation.profile.components.ChangePasswordBottomSheet
import com.moksh.imposterai.presentation.profile.components.ConfirmationBottomSheet
import com.moksh.imposterai.presentation.profile.components.ProfileLinkText
import com.moksh.imposterai.presentation.profile.viewmodel.OpenedBottomSheet
import com.moksh.imposterai.presentation.profile.viewmodel.ProfileActions
import com.moksh.imposterai.presentation.profile.viewmodel.ProfileEvent
import com.moksh.imposterai.presentation.profile.viewmodel.ProfileState
import com.moksh.imposterai.presentation.profile.viewmodel.ProfileViewModel

@Composable
fun ProfilePage(
    viewModel: ProfileViewModel = hiltViewModel(),
    onPopBack: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val state by viewModel.profileState.collectAsState()
    val context = LocalContext.current

    // Collect events
    ObserveAsEvents(viewModel.profileEvents) { event ->
        when (event) {
            is ProfileEvent.NavigateBack -> onPopBack()
            is ProfileEvent.NavigateToLogin -> onLogout()
            is ProfileEvent.OpenUrl -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                context.startActivity(intent)
            }

            is ProfileEvent.ShowDeleteAccountSuccess -> {
                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_LONG).show()
                onDeleteAccount()
            }

            is ProfileEvent.ShowMessage -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }

            is ProfileEvent.ShowError -> {
                Toast.makeText(context, event.error, Toast.LENGTH_LONG).show()
            }
        }
    }

    ProfilePageView(
        state = state,
        onAction = viewModel::handleAction
    )
}

// Action handler type
typealias ProfileActionHandler = (ProfileActions) -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfilePageView(
    state: ProfileState,
    onAction: ProfileActionHandler
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(end = 25.dp),
                title = {
                    Text(
                        "Profile Page",
                        style = MaterialTheme.typography.headlineMedium.copy(color = White)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .padding(vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            PrimaryTextField(
                value = if (state.changeUsernameEnabled) state.newUsername else state.username.ifEmpty { "moksh2" },
                onValueChange = {
                    if (state.changeUsernameEnabled) onAction(
                        ProfileActions.UpdateUsername(
                            it
                        )
                    )
                },
                readOnly = !state.changeUsernameEnabled,
                suffixIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit username",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable(onClick = { onAction(ProfileActions.EnableUsernameEdit) })
                    )
                },
                label = "Username"
            )

            PrimaryTextField(
                value = state.password.ifEmpty { "password1234" },
                onValueChange = { },
                readOnly = true,
                isPasswordField = true,
                suffixIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit password",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable(onClick = { onAction(ProfileActions.EnablePasswordEdit) })
                    )
                },
                label = "Password"
            )

            Spacer(modifier = Modifier.weight(1f))

            ProfileLinkText(
                text = "Privacy Policy",
                onClick = { onAction(ProfileActions.OpenPrivacyPolicy) }
            )

            ProfileLinkText(
                text = "Logout",
                onClick = { onAction(ProfileActions.ShowLogoutConfirmation) }
            )

            ProfileLinkText(
                text = "Delete Account",
                textColor = Color.Red,
                onClick = { onAction(ProfileActions.ShowDeleteAccountConfirmation) }
            )

            PrimaryButton(
                text = "Save",
                onClick = { onAction(ProfileActions.SaveChanges) },
                enabled = state.isButtonEnabled,
                isLoading = state.isButtonLoading
            )
        }

        // Bottom sheets
        when (state.currentBottomSheet) {
            is OpenedBottomSheet.ChangePassword -> {
                ChangePasswordBottomSheet(
                    onDismiss = { onAction(ProfileActions.ChangeBottomSheet(null)) },
                    onPasswordChange = { newPassword ->
                        onAction(ProfileActions.UpdatePassword(newPassword, state.confirmPassword))
                    },
                    onConfirmPasswordChange = { confirmPassword ->
                        onAction(ProfileActions.UpdatePassword(state.newPassword, confirmPassword))
                    },
                    onConfirm = {
                        onAction(ProfileActions.SaveChanges)
                        onAction(ProfileActions.ChangeBottomSheet(null))
                    }
                )
            }

            is OpenedBottomSheet.LogoutConfirmation -> {
                ConfirmationBottomSheet(
                    title = "Logout",
                    message = "Are you sure you want to logout?",
                    confirmButtonText = "Logout",
                    onDismiss = { onAction(ProfileActions.ChangeBottomSheet(null)) },
                    onConfirm = {
                        onAction(ProfileActions.ConfirmLogout)
                        onAction(ProfileActions.ChangeBottomSheet(null))
                    }
                )
            }

            is OpenedBottomSheet.DeleteAccountConfirmation -> {
                ConfirmationBottomSheet(
                    title = "Delete Account",
                    message = "Are you sure you want to delete your account? This action cannot be undone.",
                    confirmButtonText = "Delete Account",
                    isDestructive = true,
                    onDismiss = { onAction(ProfileActions.ChangeBottomSheet(null)) },
                    onConfirm = {
                        onAction(ProfileActions.ConfirmDeleteAccount)
                        onAction(ProfileActions.ChangeBottomSheet(null))
                    }
                )
            }

            null -> {} // No bottom sheet shown
        }
    }
}

@Composable
@Preview
private fun ProfilePagePreview() {
    ImposterAiTheme {
        ProfilePageView(
            state = ProfileState(
                username = "moksh2",
                password = "password1234",
                changeUsernameEnabled = false,
                newUsername = "",
                isButtonEnabled = false,
                isButtonLoading = false,
                currentBottomSheet = null
            ),
            onAction = {}
        )
    }
}