package com.example.cfprogresstracker.ui.components

import android.os.CountDownTimer
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.contentColorFor
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfprogresstracker.R
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.utils.Phase
import com.example.cfprogresstracker.utils.convertMillisToHMS
import com.example.cfprogresstracker.utils.loadUrl
import com.example.cfprogresstracker.utils.unixToDateAndTime
import java.util.*


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContestInfoCard(contest: Contest, modifier: Modifier = Modifier, within3days: Boolean? = null, onClickAddToCalender: () -> Unit = {}) {

    val totalTimeInMillis =
        contest.startTimeInMillis() - System.currentTimeMillis()
    var timeLeftInMillis by remember { mutableStateOf(totalTimeInMillis) }
    val onTick: (Long) -> Unit = { timeLeftInMillis = it }
    var isStarted by remember { mutableStateOf(false) }
    if (contest.phase == Phase.BEFORE && !isStarted) {
        MyCountDownTimer(totalTimeInMillis, onTik = onTick).start()
        isStarted = true
    }

    val bgColor =
        if (contest.phase == Phase.CODING) MaterialTheme.colorScheme.secondaryContainer
        else if (contest.phase == Phase.FINISHED && contest.isAttempted) MaterialTheme.colorScheme.tertiaryContainer
        else MaterialTheme.colorScheme.surface

    val contestUrl = if(contest.phase == Phase.BEFORE) contest.getContestLink() else contest.getLink()
    val context = LocalContext.current

    Card(
        onClick = {
            loadUrl(context = context, url =  contestUrl)
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        backgroundColor = bgColor,
        contentColor = contentColorFor(backgroundColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                text = contest.name,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
            )
            Divider(
                modifier = Modifier.padding(horizontal = 4.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = "Length: ${convertMillisToHMS(contest.durationInMillis())}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            val startOrFinished = if(contest.phase == Phase.FINISHED) "Dated" else "Starts On"
            Text(
                text = "$startOrFinished: ${unixToDateAndTime(contest.startTimeInMillis())}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            if (contest.phase == Phase.BEFORE) {
                within3days?.let {
                    val beforeSize = if (it) 20.sp else 18.sp
                    Text(
                        text = "Before: ${convertMillisToHMS(timeLeftInMillis)}",
                        fontSize = beforeSize,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            if (contest.phase == Phase.FINISHED) {
                /*val status = if (contest.isAttempted) "ATTEMPTED" else "UNATTEMPTED"
                Text(
                    text = "Status: $status",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )*/

                if(contest.isAttempted) {
                    Text(
                        text = "Rank: ${contest.rank}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                    val ratingChange = if (contest.ratingChange > 0) "+${contest.ratingChange}" else "${contest.ratingChange}"
                    Text(
                        text = "Rating Change: $ratingChange",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "New Rating: ${contest.newRating}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                }
            }
            if(contest.phase == Phase.BEFORE) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
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

class MyCountDownTimer(totalTimeInMillis: Long, val onTik: (Long) -> Unit) :
    CountDownTimer(totalTimeInMillis, 1000) {
    override fun onTick(p0: Long) {
        this.onTik(p0)
    }

    override fun onFinish() {}
}



