package com.moksh.imposterai.presentation.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moksh.imposterai.presentation.common.PrimaryButton
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme

@Composable
fun GameOverView(
    modifier: Modifier = Modifier,
    aiButtonLoading: Boolean,
    humanButtonLoading: Boolean,
    onHumanButtonClick: () -> Unit,
    onAiButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Text("Time over", color = MaterialTheme.colorScheme.onSecondary.copy(alpha = .75f))
        Text(
            "Who did you talk to?", style = MaterialTheme.typography.headlineMedium.copy(
                MaterialTheme.colorScheme.onSecondary
            )
        )
        Spacer(Modifier.height(20.dp))

        PrimaryButton(
            text = "Human", onClick = {
                onHumanButtonClick()
            }, isLoading = humanButtonLoading
        )
        Spacer(Modifier.height(10.dp))
        PrimaryButton(text = "AI Bot", onClick = {
            onAiButtonClick()
        }, isLoading = aiButtonLoading)
    }
}

@Preview
@Composable
private fun GameOverViewPreview() {
    ImposterAiTheme {
        GameOverView(
            aiButtonLoading = false,
            humanButtonLoading = false,
            onHumanButtonClick = {},
            onAiButtonClick = {},
        )
    }
}