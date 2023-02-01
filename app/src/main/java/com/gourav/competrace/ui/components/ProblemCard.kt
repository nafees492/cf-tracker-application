package com.gourav.competrace.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gourav.competrace.model.Contest
import com.gourav.competrace.model.Problem
import com.gourav.competrace.utils.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemCard(
    problem: Problem,
    contestListById: MutableMap<Int, Contest>,
    modifier: Modifier = Modifier,
    selectedChips: Set<String> = emptySet(),
    onClickFilterChip: (String) -> Unit = {},
) {

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
                type = "Problem",
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
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        problem.contestId?.let {
            Text(
                text = "${contestListById[it]?.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        problem.tags?.let { tags ->
            FilterChipScrollableRow(
                chipList = tags,
                selectedChips = selectedChips,
                onClickFilterChip = onClickFilterChip
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProblemCardDesign(
    rating: Int?,
    color: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(
                getCardHeight(
                    titleLargeTexts = 1,
                    bodyMediumTexts = 1,
                    extraPaddingValues = 24.dp + FilterChipDefaults.Height
                )
            )
            .animateContentSize()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1 - CardValues.TriangularFractionOfCard)
                    .fillMaxHeight(),
                content = content
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(
                        getTextWidthInDp(
                            testSize = MaterialTheme.typography.bodyMedium.fontSize,
                            letters = 4
                        )
                    )
                    .fillMaxHeight()
            ) {
                BackgroundDesignArrow(color = color)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "$rating",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}