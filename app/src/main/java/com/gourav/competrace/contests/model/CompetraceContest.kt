package com.gourav.competrace.contests.model

import android.content.Context
import com.gourav.competrace.app_core.util.*
import com.gourav.competrace.settings.util.ScheduleNotifBeforeOptions
import java.util.*

data class CompetraceContest(
    val id: Any,
    val name: String,
    val phase: String,
    val websiteUrl: String,
    val startTimeInMillis: Long,
    val durationInMillis: Long,
    val registrationUrl: String? = null,
    val site: String,
) {
    val endTimeInMillis = startTimeInMillis + durationInMillis
    val ratedCategories = arrayListOf<ContestRatedCategories>()

    var newRating: Int = 0
    var ratingChange: Int = 0
    var rank: Int = 0

   /* fun uniqueId(): Int = MessageDigest.getInstance("SHA-256")
        .digest(websiteUrl.toByteArray())
        .fold(-50) { acc, byte -> (acc shl 8) or byte.toInt() }*/

    private fun uniqueId(): Int {
        return UUID.nameUUIDFromBytes(websiteUrl.toByteArray()).mostSignificantBits.toInt()
    }

    private fun getNotificationMessage(timeBeforeStart: Int) = buildString {
        append(name)
        append(" is going to start at ")
        append(TimeUtils.unixToTime(startTimeInMillis))
        append(". Hurry Up!!\n")
        append(ScheduleNotifBeforeOptions.getOption(timeBeforeStart))
        append(" to Go.\n")
    }

    fun getAlarmItem(timeBeforeStart: Int) = ContestAlarmItem(
        id = uniqueId(),
        contestId = id.toString(),
        timeInMillis = startTimeInMillis - TimeUtils.minutesToMillis(timeBeforeStart),
        title = site,
        message = getNotificationMessage(timeBeforeStart),
        registrationUrl = registrationUrl ?: ""
    )

    fun addToCalender(context: Context) {
        context.addEventToCalendar(
            title = name,
            startTime = startTimeInMillis,
            endTime = endTimeInMillis,
            location = websiteUrl,
            description = "Add Contest to Calender"
        )
    }

    private fun daysLeft(): Long = (startTimeInMillis - TimeUtils.currentTimeInMillis()) / (24 * 3600 * 1000)

    private fun within7Days(): Boolean = daysLeft() in Long.MIN_VALUE..6L

    fun registrationOpen(): Boolean = if(site == Sites.Codeforces.title) daysLeft() in 0L..1L else false

    fun isOngoing() = phase == Phase.CODING

    fun isWithin7Days() = phase == Phase.BEFORE && within7Days()

    fun isAfter7Days() = phase == Phase.BEFORE && !within7Days()
}

