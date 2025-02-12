package com.moksh.imposterai.presentation.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme
import com.moksh.imposterai.presentation.utils.toTimerString


@Composable
fun ChatScreenAppBar(
    modifier: Modifier = Modifier,
    timeLeft: Int,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            "ImposterAI ?", style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.onSecondary
            )
        )
        Text(
            timeLeft.toTimerString(), style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

@Composable
@Preview
private fun ChatScreenAppBarPreview() {
    ImposterAiTheme {
        ChatScreenAppBar(
            timeLeft = 120
        )
    }
}