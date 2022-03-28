package com.example.cfprogresstracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp


@Composable
fun NormalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconArrangement: Arrangement.Horizontal = Arrangement.Start,
    iconDescription: String = "",
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        modifier = modifier,
        enabled = enabled
    ) {
        if (icon != null && iconArrangement == Arrangement.Start)
            Icon(
                imageVector = icon,
                contentDescription = iconDescription
            )
        Text(
            text = text.uppercase(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (icon != null && iconArrangement == Arrangement.End)
            Icon(
                imageVector = icon,
                contentDescription = iconDescription
            )
    }
}