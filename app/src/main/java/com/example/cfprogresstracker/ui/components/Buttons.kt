package com.example.cfprogresstracker.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp


@Composable
fun NormalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable () -> Unit = {  },
    trailingIcon: @Composable () -> Unit = {  },
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        modifier = modifier,
        enabled = enabled
    ) {
        leadingIcon()
        Text(
            text = text.uppercase(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        trailingIcon()
    }
}