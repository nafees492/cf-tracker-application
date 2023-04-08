package com.gourav.competrace.contests.presentation

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceIconButton
import com.gourav.competrace.app_core.ui.components.TwoStateAnimatedIconButton
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.contests.util.MyCountDownTimer
import com.gourav.competrace.app_core.ui.theme.RegistrationRed
import com.gourav.competrace.app_core.util.*
import java.sql.Time

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContestCard(
    contest: CompetraceContest,
    modifier: Modifier = Modifier,
    notificationContestIdList: Set<String>,
    onClickNotificationIcon: (CompetraceContest) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    @Composable
    fun ratedCategoriesRow(modifier1: Modifier) {
        LazyRow(
            modifier = modifier1,
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

            item {
                if (contest.registrationOpen() && contest.phase == Phase.BEFORE) {
                    ElevatedAssistChip(
                        onClick = {
                            context.loadUrl(url = contest.registrationUrl)
                        },
                        label = {
                            Text(
                                text = stringResource(id = R.string.register_now),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                        colors = AssistChipDefaults.elevatedAssistChipColors(
                            containerColor = RegistrationRed,
                            labelColor = Color.White
                        )
                    )
                }
            }
        }
    }

    val onClickContestCard: () -> Unit = {
        context.loadUrl(url = contest.websiteUrl)
    }

    val onLongClickContestCard: () -> Unit = {
        context.copyTextToClipBoard(
            textToCopy = contest.websiteUrl,
            toastMessageId = R.string.contest_link_copied,
            clipboardManager = clipboardManager,
            haptic = haptic
        )
    }

    val cardBgColor =
        if (contest.phase == Phase.CODING) MaterialTheme.colorScheme.secondaryContainer
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

                val startTime = TimeUtils.unixToDMET(contest.startTimeInMillis)
                val length = TimeUtils.getFormattedTime(contest.durationInMillis, format = "%02d:%02d")

                val startTimeAndLength = buildAnnotatedString {
                    append(startTime)
                    append("  |  ")
                    append(length)
                }

                Text(
                    text = startTimeAndLength,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f),
                    modifier = Modifier.padding(bottom = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (contest.isWithin7Days()) {
                    val totalTimeInMillis =
                        if (contest.phase == Phase.BEFORE)
                            contest.startTimeInMillis - TimeUtils.currentTimeInMillis()
                        else
                            contest.endTimeInMillis - TimeUtils.currentTimeInMillis()

                    var timeLeftInMillis by remember { mutableStateOf(totalTimeInMillis) }

                    var isStarted by remember { mutableStateOf(false) }
                    if (!isStarted) {
                        MyCountDownTimer(
                            totalTimeInMillis = totalTimeInMillis,
                            onTik = { timeLeftInMillis = it }
                        ).start()
                        isStarted = true
                    }

                    AssistChip(
                        onClick = { /*TODO*/ },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_timer_24px),
                                contentDescription = "Timer icon",
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        },
                        label = {
                            Text(
                                text = TimeUtils.getFormattedTime(timeLeftInMillis),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                    )
                } else ratedCategoriesRow(Modifier)

                if (contest.phase == Phase.BEFORE) Row {
                    CompetraceIconButton(
                        iconId = R.drawable.ic_calendar_add_on_24px,
                        onClick = { contest.addToCalender(context = context) },
                        contentDescription = stringResource(R.string.cd_add_to_calender),
                    )

                    TwoStateAnimatedIconButton(
                        onStateIconId = R.drawable.ic_notifications_active_filled_24px,
                        offStateIconId = R.drawable.ic_notifications_24px,
                        isOn = contest.id.toString() in notificationContestIdList,
                        onClick = { onClickNotificationIcon(contest) },
                        contentDescription = stringResource(R.string.cd_toggle_notif)
                    )
                }
            }
            if (contest.isWithin7Days()) ratedCategoriesRow(Modifier.padding(horizontal = 8.dp))
        }
    }
}