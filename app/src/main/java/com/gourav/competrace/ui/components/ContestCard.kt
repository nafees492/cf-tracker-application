package com.gourav.competrace.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Card
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gourav.competrace.R
import com.gourav.competrace.model.Contest
import com.gourav.competrace.utils.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContestCard(
    contest: Contest,
    modifier: Modifier = Modifier,
    within7Days: Boolean = false
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

    val ratedRow: @Composable (Modifier) -> Unit = {
        LazyRow(
            modifier = it.padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contest.rated.size) {
                ElevatedCard {
                    Text(
                        text = contest.rated[it],
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }

    val commonContent: @Composable () -> Unit = {
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

            if (contest.phase == Phase.BEFORE) {
                val (dayslen, hlen, mlen, slen) = convertMillisToDHMS(contest.durationInMillis())
                val startTime = unixToDateDayTime(contest.startTimeInMillis())

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = startTime,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    )
                    Text(
                        text = "|",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    )
                    Text(
                        text = "Length: ${formatLength(dayslen, hlen, mlen, slen)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Text(
                    text = unixToDateAndTime(contest.startTimeInMillis()),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    val onClickContestCard: () -> Unit = {
        loadUrl(context = context, url = contestUrl)
    }
    val onLongClickContestCard: () -> Unit = {
        copyTextToClipBoard(
            text = contestUrl,
            type = "Contest",
            context = context,
            clipboardManager = clipboardManager,
            haptic = haptic
        )
    }

    if (contest.phase == Phase.FINISHED && contest.isAttempted) {
        val ratingChange =
            if (contest.ratingChange > 0) "+${contest.ratingChange}" else "${contest.ratingChange}"

        val ratingChangeContainerColor =
            getRatingChangeContainerColor(ratingChange = contest.ratingChange)

        FinishedContestCardDesign(
            ratingChange = ratingChange,
            color = ratingChangeContainerColor,
            onClick = onClickContestCard,
            onLongClick = onLongClickContestCard,
            modifier = modifier
        ) {
            commonContent()
            Row(modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)) {
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
            ratedRow(Modifier.padding(horizontal = 8.dp))
        }
    } else {
        val cardBgColor = if (contest.phase == Phase.CODING) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.surfaceVariant
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
                .animateContentSize()
                .combinedClickable(
                    onClick = onClickContestCard,
                    onLongClick = onLongClickContestCard,
                ),
            backgroundColor = cardBgColor,
            contentColor = contentColorFor(backgroundColor = cardBgColor),
            shape = MaterialTheme.shapes.medium
        ) {
            Column {
                commonContent()
                if (contest.phase == Phase.BEFORE) {
                    val (days, h, m, s) = convertMillisToDHMS(timeLeftInMillis)
                    val startPhrase = if (h + m + s != 0L) when (days) {
                        0L -> ""
                        1L -> "$days day, "
                        else -> "$days days, "
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
                        if (within7Days) AssistChip(
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
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (h + m + s != 0L) {
                                        Text(
                                            text = timeStamp,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = " hrs",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            },
                        ) else ratedRow(Modifier)

                        NormalIconButton(
                            iconId = R.drawable.ic_calendar_add_on_24px,
                            onClick = { contest.addToCalender(context = context) },
                            contentDescription = "Add to calender",
                        )
                    }
                }
                if (within7Days || contest.phase == Phase.FINISHED) ratedRow(
                    Modifier.padding(
                        horizontal = 8.dp
                    )
                )
            }
        }
    }
}