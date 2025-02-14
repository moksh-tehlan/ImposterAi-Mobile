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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moksh.imposterai.presentation.common.PrimaryButton
import com.moksh.imposterai.presentation.core.theme.Black
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme
import com.moksh.imposterai.presentation.core.theme.White

@Composable
fun AfterGuessView(
    modifier: Modifier = Modifier,
    isBot: Boolean,
    playerWon: Boolean,
    onNewGame: () -> Unit,
    isButtonLoading: Boolean = false,
) {

    val resultText = if (playerWon) "SPOT ON!" else "WRONG!"
    val supportingText =
        if (playerWon) "It takes one to know one!" else "Singularity is just around the corner"
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Text(resultText, color = MaterialTheme.colorScheme.onSecondary)
        Spacer(Modifier.height(20.dp))
        Text(
            buildAnnotatedString {
                append("You just talked to ${if (isBot) "An" else "a fellow"} ")
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    append(if (isBot) "AI Bot" else "Human")
                }
            }, style = MaterialTheme.typography.headlineMedium.copy(
                MaterialTheme.colorScheme.onSecondary
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(supportingText, color = MaterialTheme.colorScheme.onSecondary.copy(alpha = .75f))
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
private fun AfterGuessViewPreview() {
    ImposterAiTheme {
        AfterGuessView(
            isBot = true,
            playerWon = true,
            onNewGame = {}
        )
    }
}