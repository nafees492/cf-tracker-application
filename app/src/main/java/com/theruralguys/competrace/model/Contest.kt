package com.theruralguys.competrace.model

import java.util.*

data class Contest(
    val id: Int,
    val name: String,
    val type: String,
    val phase: String,
    val frozen: Boolean,
    val durationSeconds: Int,
    val startTimeSeconds: Int?,
    val relativeTimeSeconds: Int?,
    val difficulty: Int?,
    val kind: String?,
) {
    var isAttempted: Boolean = false
    var ratingChange: Int = 0
    var rank: Int = 0
    var newRating: Int = 0
    fun getLink() = "https://codeforces.com/contest/$id"
    fun getContestLink() = "https://codeforces.com/contests/$id"
    fun startTimeInMillis() = startTimeSeconds!!.toLong() * 1000L
    fun endTimeInMillis() = (startTimeSeconds!!.toLong() + durationSeconds.toLong()) * 1000L
    fun durationInMillis() = (durationSeconds.toLong()) * 1000L
    fun getContestDate() = Date(startTimeInMillis())
}