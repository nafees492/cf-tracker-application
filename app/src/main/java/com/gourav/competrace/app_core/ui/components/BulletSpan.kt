package com.gourav.competrace.app_core.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle


@Composable
fun BulletSpan(
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    val bullet = "\u2022"
    Text(
        text = bullet,
        style = style,
        modifier = modifier
    )
}