package com.moksh.imposterai.presentation.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.moksh.imposterai.presentation.core.theme.White

@Composable
fun ProfileLinkText(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = Color.White,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = textColor
            )
        )
        Icon(
            Icons.Default.KeyboardArrowRight,
            "Privacy Policy",
            tint = White,
        )
    }
}