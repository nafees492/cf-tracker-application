package com.gourav.competrace.progress.participated_contests.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.util.copyTextToClipBoard
import com.gourav.competrace.app_core.util.loadUrl
import com.gourav.competrace.app_core.util.unixToDMYETZ
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.ui.components.BackgroundDesignArrow
import com.gourav.competrace.utils.*
import com.gourav.competrace.app_core.util.CardValues.TriangularFractionOfCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipatedContestCard(
    contest: CompetraceContest,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    val ratingChange =
        if (contest.ratingChange > 0) "+${contest.ratingChange}" else "${contest.ratingChange}"

    val ratingChangeContainerColor =
        getRatingChangeContainerColor(ratingChange = contest.ratingChange)

    val onClickContestCard: () -> Unit = {
        loadUrl(context = context, url = contest.websiteUrl)
    }

    val onLongClickContestCard: () -> Unit = {
        context.copyTextToClipBoard(
            textToCopy = contest.websiteUrl,
            toastMessageId = R.string.contest_link_copied,
            clipboardManager = clipboardManager,
            haptic = haptic
        )
    }

    ParticipatedContestCardDesign(
        ratingChange = ratingChange,
        color = ratingChangeContainerColor,
        onClick = onClickContestCard,
        onLongClick = onLongClickContestCard,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = contest.name,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = unixToDMYETZ(contest.startTimeInMillis),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            val rankAndRating = buildAnnotatedString {
                append("Rank: " + "${contest.rank}")
                append("  |  ")
                append("New Rating: ")
                withStyle(SpanStyle(color = getRatingTextColor(rating = contest.newRating))){
                    append(contest.newRating.toString())
                }
            }

            Text(
                text = rankAndRating,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(contest.ratedCategories.size) {
                    ElevatedAssistChip(onClick = { /*TODO*/ }, label = {
                        Text(
                            text = contest.ratedCategories[it].value,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ParticipatedContestCardDesign(
    ratingChange: String,
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
                    bodyMediumTexts = 2,
                    extraPaddingValues = FilterChipDefaults.Height + 32.dp
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
                    .weight(1 - TriangularFractionOfCard)
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
                        text = ratingChange,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}