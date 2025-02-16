package com.moksh.imposterai.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moksh.imposterai.presentation.common.PrimaryButton
import com.moksh.imposterai.presentation.core.theme.Black
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme
import com.moksh.imposterai.presentation.core.theme.White

@Composable
fun PlayerLeftView(
    modifier: Modifier = Modifier,
    isButtonLoading: Boolean = false,
    onNewGame: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Text("Oops!!", color = MaterialTheme.colorScheme.onSecondary)
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Your opponent has left the match",
            style = MaterialTheme.typography.headlineMedium.copy(
                MaterialTheme.colorScheme.onSecondary
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "You can always start a new game",
            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = .75f)
        )
        Spacer(Modifier.height(20.dp))

        PrimaryButton(
            modifier = Modifier.background(White),
            text = "New Game",
            isLoading = isButtonLoading,
            onClick = onNewGame,
            buttonColors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.onSecondary,
                contentColor = Black,
                disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
                disabledContentColor = Black,
            )
        )
    }
}

@Composable
@Preview
private fun PlayerLeftViewPreview() {
    ImposterAiTheme {
        PlayerLeftView(
            onNewGame = {}
        )
    }
}