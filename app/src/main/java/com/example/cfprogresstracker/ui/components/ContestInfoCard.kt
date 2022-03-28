package com.example.cfprogresstracker.ui.components

import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.contentColorFor
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.viewmodel.Phase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContestInfoCard(contest: Contest, within3days: Boolean? = null) {

    val totalTimeInMillis =
        contest.startTimeSeconds!!.toLong() * 1000 - System.currentTimeMillis()
    var timeLeftInMillis by remember { mutableStateOf(totalTimeInMillis) }
    val onTick: (Long) -> Unit = { timeLeftInMillis = it }
    var isStarted by remember { mutableStateOf(false) }
    if (contest.phase == Phase.BEFORE && !isStarted) {
        MyCountDownTimer(totalTimeInMillis, onTik = onTick).start()
        isStarted = true
    }

    val bgColor =
        if (contest.phase == Phase.CODING) MaterialTheme.colorScheme.secondaryContainer
        else if (contest.phase == Phase.FINISHED && contest.hasSubmissions) MaterialTheme.colorScheme.tertiaryContainer
        else MaterialTheme.colorScheme.surface

    Card(
        onClick = { /*TODO*/ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
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
                text = "Length: ${convertMillisToHMS(contest.durationSeconds * 1000L)}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "Starts On: ${unixToDateAndTime(contest.startTimeSeconds.toLong())}",
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
                val status = if (contest.hasSubmissions) "ATTEMPTED" else "UNATTEMPTED"
                Text(
                    text = "Status: $status",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
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

@SuppressLint("DefaultLocale")
private fun convertMillisToHMS(timeInMilliSec: Long): String {
    var millis = timeInMilliSec
    val days = millis / (24 * 3600000)
    millis %= (24 * 3600000)
    val hms = java.lang.String.format(
        "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(millis)
        ),
        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(millis)
        )
    )
    return (when {
        days > 1 -> "$days days"
        days == 1L -> "$days day"
        else -> ""
    }) + (if (hms != "00:00:00") ", and $hms hrs" else "")
}

@SuppressLint("SimpleDateFormat")
private fun unixToDateAndTime(timeStamp: Long): String {
    val format = SimpleDateFormat("d MMM yyyy, EEE hh:mm a z")
    return format.format(Date(timeStamp * 1000))
}