package com.gourav.competrace.problemset.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.gourav.competrace.app_core.ui.components.FilterChipScrollableRow
import com.gourav.competrace.problemset.model.CompetraceProblem
import com.gourav.competrace.app_core.ui.components.BackgroundDesignArrow
import com.gourav.competrace.R
import com.gourav.competrace.app_core.util.*

@Composable
fun ProblemCard(
    problem: CompetraceProblem,
    contestName: String?,
    isTagsVisible: Boolean,
    selectedTags: Set<String>,
    onClickFilterChip: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    val ratingContainerColor = ColorUtils.getRatingContainerColor(rating = problem.rating)

    ProblemCardDesign(
        rating = problem.rating,
        color = ratingContainerColor,
        onClick = {
            context.loadUrl(url = problem.websiteUrl)
        },
        onLongClick = {
            context.copyTextToClipBoard(
                textToCopy = problem.websiteUrl,
                toastMessageId = R.string.problem_link_copied,
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

        Text(
            text = contestName.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 8.dp),
            maxLines = if(isTagsVisible) 1 else 2,
            overflow = TextOverflow.Ellipsis
        )

        problem.tags?.let { tags ->
            if(isTagsVisible) FilterChipScrollableRow(
                chipList = tags,
                selectedChips = selectedTags,
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
                SizeUtil.getCardHeight(
                    titleLargeTexts = 1,
                    bodyMediumTexts = 1,
                    extraPaddingValues = 24.dp + FilterChipDefaults.Height
                )
            )
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
                        SizeUtil.getTextWidthInDp(
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