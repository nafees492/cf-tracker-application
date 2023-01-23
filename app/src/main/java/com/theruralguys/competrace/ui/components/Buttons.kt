package com.theruralguys.competrace.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp


@Composable
fun NormalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable () -> Unit = { },
    trailingIcon: @Composable () -> Unit = { },
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        modifier = modifier,
        enabled = enabled,
        shape = MaterialTheme.shapes.small
    ) {
        leadingIcon()
        Text(
            text = text.trim().uppercase(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        trailingIcon()
    }
}

@Composable
fun NormalIconButton(
    iconId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    contentDescription: String? = null,
    enabled: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled
        ) {
            Icon(
                painter = painterResource(id = iconId), contentDescription = contentDescription
            )
        }
        text?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(48.dp)
            )
        }
    }

}