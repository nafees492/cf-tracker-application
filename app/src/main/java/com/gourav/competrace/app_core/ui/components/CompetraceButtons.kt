package com.gourav.competrace.app_core.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R


@Composable
fun CompetraceButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable () -> Unit = { },
    trailingIcon: @Composable () -> Unit = { },
    enabled: Boolean = true,
    maxLines: Int = 1
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
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        trailingIcon()
    }
}

@Composable
fun CompetraceIconButton(
    @DrawableRes iconId: Int,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    text: String? = null,
    enabled: Boolean = true,
    iconColor: Color = LocalContentColor.current
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = iconColor
            ),
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = contentDescription
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetraceFilterIconButton(isActive: Boolean, badgeCondition: Boolean, onClick: () -> Unit) {
    val bgColorForFilterIcon =
        if (isActive) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surface

    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = bgColorForFilterIcon,
        )
    ) {
        BadgedBox(
            badge = { if (badgeCondition) Badge() },
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter_list_24px),
                contentDescription = stringResource(id = R.string.filter),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetraceBadgeIconButton(
    badgeCondition: Boolean,
    onClick: () -> Unit,
    @DrawableRes iconId: Int,
    contentDescription: String?
) {
    IconButton(onClick = onClick) {
        BadgedBox(
            badge = { if (badgeCondition) Badge() },
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = contentDescription,
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TwoStateAnimatedIconButton(
    isOn: Boolean,
    @DrawableRes onStateIconId: Int,
    @DrawableRes offStateIconId: Int,
    onClick: () -> Unit,
    contentDescription: String? = null
) {
    IconButton(onClick = onClick) {
        AnimatedContent(
            targetState = isOn,
            transitionSpec = {
                scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    initialScale = 0.2f
                ) + fadeIn() with scaleOut(
                    animationSpec = tween(durationMillis = 50),
                    targetScale = 0.2f
                ) + fadeOut() using (
                    SizeTransform(clip = false)
                )
            }
        ) {
            val iconId = if (it) onStateIconId else offStateIconId
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = contentDescription,
                modifier = Modifier
            )
        }
    }
}
