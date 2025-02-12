package com.moksh.imposterai.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme

@Composable
fun ChatTextField(
    modifier: Modifier = Modifier,
    value: String,
    isMyChat: Boolean = true,
    isEnabled: Boolean = false,
    onValueChange: ((String) -> Unit)? = null,
    onMessageSent: (() -> Unit)? = null,
    isLoading: Boolean = false,
) {
    val keyboardOptions = remember {
        KeyboardOptions(imeAction = ImeAction.Send)
    }
    val keyboardActions = remember(onMessageSent) {
        KeyboardActions(onSend = { onMessageSent?.invoke() })
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = if (isMyChat) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 250.dp, min = 200.dp)
                .background(
                    if (isMyChat)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSecondary
                )
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .heightIn(min = 40.dp),
            contentAlignment = if (isLoading) Alignment.Center else Alignment.CenterStart
        ) {
            if (isLoading) {
                LoadingIndicator()
            } else {
                BasicTextField(
                    value = value,
                    enabled = isEnabled,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                    onValueChange = { onValueChange?.invoke(it) },
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
@Preview
private fun ChatTextFieldPreview() {
    ImposterAiTheme {
        ChatTextField(
            isMyChat = false,
            isEnabled = false,
            isLoading = true,
            value = "hello"
        )
    }
}