package com.gourav.competrace.contests.model

import android.content.Context
import com.gourav.competrace.utils.addEventToCalendar
import java.util.*

data class Contest(
    val id: Int = 0,
    val name: String = "",
    val type: String = "",
    val phase: String = "",
    val frozen: Boolean = false,
    val durationSeconds: Int = 0,
    val startTimeSeconds: Int?,
    val relativeTimeSeconds: Int?,
    val difficulty: Int?,
    val kind: String?,
    val preparedBy: String?,
    val websiteUrl: String?,
    val description: String?,
    val icpcRegion: String?,
    val country: String?,
    val city: String?,
    val season: String?
) {
    var isAttempted: Boolean = false
    var ratingChange: Int = 0
    var rank: Int = 0
    var newRating: Int = 0
    val rated = arrayListOf<String>()
    private val gymOrContest = if(id > 100000) "gym" else "contest"
    fun getLink() = "https://codeforces.com/$gymOrContest/$id"
    fun getLinkPhaseBefore() = "https://codeforces.com/contests/$id"
    fun startTimeInMillis() = startTimeSeconds!!.toLong() * 1000L
    fun endTimeInMillis() = (startTimeSeconds!!.toLong() + durationSeconds.toLong()) * 1000L
    fun durationInMillis() = (durationSeconds.toLong()) * 1000L
    fun getContestDate() = Date(startTimeInMillis())

    fun addToCalender(context: Context) {
        addEventToCalendar(
            context = context,
            title = name,
            startTime = startTimeInMillis(),
            endTime = endTimeInMillis(),
            location = getLinkPhaseBefore(),
            description = ""
        )
    }
}