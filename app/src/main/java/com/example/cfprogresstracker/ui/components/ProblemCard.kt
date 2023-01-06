package com.example.cfprogresstracker.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.model.Problem
import com.example.cfprogresstracker.utils.getRatingContainerColor
import com.example.cfprogresstracker.utils.loadUrl

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
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

    val ratingContainerColor = getRatingContainerColor(rating = problem.rating)

    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    ProblemCardDesign(
        rating = problem.rating,
        color = ratingContainerColor,
        onClick = {
            loadUrl(context = context, url = problem.getLinkViaProblemSet())
        },
        onLongClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            clipboardManager.setText(AnnotatedString(problem.getLinkViaProblemSet()))
            Toast.makeText(context, "Problem Link Copied", Toast.LENGTH_SHORT).show()
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