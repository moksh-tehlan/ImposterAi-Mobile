package com.moksh.imposterai.presentation.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme
import com.moksh.imposterai.presentation.core.theme.White

@Composable
fun PrimaryTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPasswordField: Boolean = false,
    readOnly: Boolean = false,
    suffixIcon: (@Composable () -> Unit)? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
        focusedTextColor = White,
        unfocusedTextColor = White,
        focusedPlaceholderColor = White.copy(.5f),
        unfocusedPlaceholderColor = White.copy(.5f),
        focusedSupportingTextColor = White.copy(.5f),
        unfocusedSupportingTextColor = White.copy(.5f),
    )
) {

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        label = { Text(label, color = Color.White) },
        suffix = suffixIcon,
        colors = colors,
        keyboardOptions = if (isPasswordField) {
            KeyboardOptions(keyboardType = KeyboardType.Password)
        } else KeyboardOptions.Default,
        visualTransformation = if (isPasswordField) {
            PasswordVisualTransformation()
        } else {
            androidx.compose.ui.text.input.VisualTransformation.None
        }
    )
}

@Composable
@Preview
private fun PrimaryTextFieldPreview() {
    ImposterAiTheme {
        PrimaryTextField(
            value = "",
            onValueChange = {},
            label = "Username",
            isPasswordField = true
        )
    }
}
