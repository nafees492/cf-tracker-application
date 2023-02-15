package com.gourav.competrace.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp


@Composable
fun CompetraceButton(
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
fun CompetraceIconButton(
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
                modifier = Modifier.width(54.dp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompetraceClickableText(
    text: AnnotatedString,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    onLongClick: (() -> Unit)? = null,
) {
    Text(
        text = text,
        style = style,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 2.dp, horizontal = 4.dp)
    )
}