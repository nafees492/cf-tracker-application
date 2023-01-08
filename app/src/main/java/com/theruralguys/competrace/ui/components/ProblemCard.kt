package com.theruralguys.competrace.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theruralguys.competrace.model.Contest
import com.theruralguys.competrace.model.Problem
import com.theruralguys.competrace.utils.copyTextToClipBoard
import com.theruralguys.competrace.utils.getRatingContainerColor
import com.theruralguys.competrace.utils.loadUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemCard(
    problem: Problem,
    contestListById: MutableMap<Int, Contest>,
    modifier: Modifier = Modifier,
    selectedChips: Set<String> = emptySet(),
    onClickFilterChip: (String) -> Unit = {},
) {
    val isSelected: (String) -> Boolean = { selectedChips.contains(it) }

    val trailingIcon: @Composable ((visible: Boolean) -> Unit) = {
        AnimatedVisibility(visible = it) {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = ""
            )
        }
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    val ratingContainerColor = getRatingContainerColor(rating = problem.rating)

    ProblemCardDesign(
        rating = problem.rating,
        color = ratingContainerColor,
        onClick = {
            loadUrl(context = context, url = problem.getLinkViaProblemSet())
        },
        onLongClick = {
            copyTextToClipBoard(
                text = problem.getLinkViaProblemSet(),
                context = context,
                clipboardManager = clipboardManager,
                haptic = haptic
            )
        },
        modifier = modifier
    ) {
        Text(
            text = "${problem.index}. ${problem.name}",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        problem.contestId?.let {
            Text(
                text = "Contest: ${contestListById[it]?.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        problem.tags?.let { tags ->
            LazyRow(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
            ) {
                items(tags.size) {
                    FilterChip(
                        selected = isSelected(tags[it]),
                        onClick = { onClickFilterChip(tags[it]) },
                        label = { Text(text = tags[it], style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = { trailingIcon(isSelected(tags[it])) },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        shape = RectangleShape,
                    )
                }
            }
        }
    }
}