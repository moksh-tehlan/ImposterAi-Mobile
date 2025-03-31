package com.moksh.imposterai.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moksh.imposterai.presentation.common.PrimaryButton
import com.moksh.imposterai.presentation.common.PrimaryTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordBottomSheet(
    onDismiss: () -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordValid by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Change Password",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            PrimaryTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    onPasswordChange(it)
                    isPasswordValid = newPassword == confirmPassword && newPassword.isNotEmpty()
                },
                isPasswordField = true,
                label = "New Password"
            )

            PrimaryTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    onConfirmPasswordChange(it)
                    isPasswordValid = newPassword == confirmPassword && newPassword.isNotEmpty()
                },
                isPasswordField = true,
                label = "Confirm Password"
            )

            PrimaryButton(
                text = "Update Password",
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                enabled = isPasswordValid
            )
        }
    }
}