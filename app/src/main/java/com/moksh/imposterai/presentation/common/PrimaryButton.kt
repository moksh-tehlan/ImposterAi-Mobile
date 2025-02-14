package com.moksh.imposterai.presentation.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moksh.imposterai.presentation.core.theme.Black


@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    isLoading: Boolean = false,
    buttonColors: ButtonColors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Black,
        disabledContainerColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = Black,
    ),
) {
    Button(
        onClick = {
            if (!isLoading) {
                onClick()
            }
        },
        colors = buttonColors,
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(30.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(text = text, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

