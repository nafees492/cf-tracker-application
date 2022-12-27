package com.example.cfprogresstracker.ui.components

import android.os.CountDownTimer
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfprogresstracker.R
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.ui.theme.CorrectGreen
import com.example.cfprogresstracker.ui.theme.IncorrectRed
import com.example.cfprogresstracker.utils.*


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContestInfoCard(
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

    val gradientColors = getRatingGradientColor(rating = contest.newRating)

    val gradientBrush = Brush.radialGradient(
        0.0f to gradientColors[1],
        1f to gradientColors[0],
        radius = 800f
    )

    val bgColor =
        if (contest.phase == Phase.CODING) MaterialTheme.colorScheme.primaryContainer
        else if (contest.phase == Phase.FINISHED && contest.isAttempted) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceVariant

    val contestUrl =
        if (contest.phase == Phase.BEFORE) contest.getContestLink() else contest.getLink()
    val context = LocalContext.current

    Card(
        onClick = {
            loadUrl(context = context, url = contestUrl)
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        //backgroundColor = bgColor,
        //contentColor = contentColorFor(backgroundColor = bgColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
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
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                val startOrFinished = if (contest.phase == Phase.FINISHED) "Dated" else "Starts On"
                Text(
                    text = "$startOrFinished: ${unixToDateAndTime(contest.startTimeInMillis())}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp, start = 8.dp, end = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

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
                if (contest.phase == Phase.BEFORE) {
                    AssistChip(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.padding(horizontal = 8.dp),
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onClickAddToCalender) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_calendar_add_on_24px),
                                contentDescription = "Add Event to Calender"
                            )
                        }
                    }
                }

                if (contest.phase == Phase.FINISHED) {
                    if (contest.isAttempted) {

                        Text(
                            text = "Rank: " + "${contest.rank}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )

                        val ratingChange =
                            if (contest.ratingChange > 0) "+${contest.ratingChange}" else "${contest.ratingChange}"
                        val ratingChangeTextColor =
                            if (contest.ratingChange > 0) CorrectGreen else IncorrectRed

                        Row(
                            modifier = Modifier.padding(bottom = 4.dp, start = 8.dp, end = 8.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "Rating Change: ",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = ratingChange,
                                style = MaterialTheme.typography.titleMedium,
                                color = ratingChangeTextColor
                            )
                        }

                        contest.newRating.let {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "New Rating: $it",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

class MyCountDownTimer(totalTimeInMillis: Long, val onTik: (Long) -> Unit) :
    CountDownTimer(totalTimeInMillis, 1000) {
    override fun onTick(p0: Long) {
        this.onTik(p0)
    }

    override fun onFinish() {}
}



