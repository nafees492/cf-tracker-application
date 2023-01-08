package com.theruralguys.competrace.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theruralguys.competrace.R
import com.theruralguys.competrace.model.Contest
import com.theruralguys.competrace.ui.theme.light_CorrectGreenContainer
import com.theruralguys.competrace.ui.theme.light_IncorrectRedContainer
import com.theruralguys.competrace.utils.*


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContestCard(
    contest: Contest,
    modifier: Modifier = Modifier,
    within3days: Boolean? = null,
    onClickAddToCalender: () -> Unit = {}
) {

    val totalTimeInMillis =
        contest.startTimeInMillis() - System.currentTimeMillis()
    var timeLeftInMillis by remember { mutableStateOf(totalTimeInMillis) }
    val onTick: (Long) -> Unit = { timeLeftInMillis = it }
    var isStarted by remember { mutableStateOf(false) }
    if (contest.phase == Phase.BEFORE && !isStarted) {
        MyCountDownTimer(totalTimeInMillis, onTik = onTick).start()
        isStarted = true
    }

    val contestUrl =
        if (contest.phase == Phase.BEFORE) contest.getContestLink() else contest.getLink()

    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    val commonContent: @Composable ColumnScope.() -> Unit = {
        Text(
            text = contest.name,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val (dayslen, hlen, mlen, slen) = convertMillisToDHMS(contest.durationInMillis())
        Text(
            text = "Length: ${formatLength(dayslen, hlen, mlen, slen)}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val startOrFinished = if (contest.phase == Phase.FINISHED) "Dated" else "Starts On"
        Text(
            text = "$startOrFinished: ${unixToDateAndTime(contest.startTimeInMillis())}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp, start = 8.dp, end = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    val onClickContestCard: () -> Unit = {
        loadUrl(context = context, url = contestUrl)
    }
    val onLongClickContestCard: () -> Unit = {
        copyTextToClipBoard(
            text = contestUrl,
            context = context,
            clipboardManager = clipboardManager,
            haptic = haptic
        )
    }

    if (contest.phase == Phase.FINISHED && contest.isAttempted) {
        val ratingChange =
            if (contest.ratingChange > 0) "+${contest.ratingChange}" else "${contest.ratingChange}"

        val ratingChangeContainerColor =
            if (contest.ratingChange > 0) light_CorrectGreenContainer else light_IncorrectRedContainer

        FinishedContestCardDesign(
            ratingChange = ratingChange,
            color = ratingChangeContainerColor,
            onClick = onClickContestCard,
            onLongClick = onLongClickContestCard,
            modifier = modifier
        ) {
            commonContent()
            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(
                    text = "Rank: " + "${contest.rank}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = "|",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Text(
                    text = "New Rating: ${contest.newRating}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    } else {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .animateContentSize()
                .combinedClickable(
                    onClick = onClickContestCard,
                    onLongClick = onLongClickContestCard,
                ),
            shape = RectangleShape,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Column {
                commonContent()
                if (contest.phase == Phase.BEFORE) {
                    val (days, h, m, s) = convertMillisToDHMS(timeLeftInMillis)
                    val startPhrase = if (h + m + s != 0L) when (days) {
                        0L -> ""
                        1L -> "$days day and "
                        else -> "$days days and "
                    } else when (days) {
                        1L -> "$days day"
                        else -> "$days days"
                    }
                    val timeStamp = String.format("%02d:%02d:%02d", h, m, s)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = { /*TODO*/ },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_timer_24px),
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            },
                            label = {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = startPhrase,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (h + m + s != 0L) {
                                        Text(
                                            text = timeStamp,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = " hrs",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        )

                        IconButton(onClick = onClickAddToCalender) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_calendar_add_on_24px),
                                contentDescription = "Add Event to Calender"
                            )
                        }
                    }
                }
            }
        }
    }
}