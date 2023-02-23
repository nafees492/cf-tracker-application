package com.gourav.competrace.contests.model

import com.gourav.competrace.app_core.util.getCurrentTimeInMillis
import com.gourav.competrace.utils.Phase

data class CodeforcesContest(
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
    private val gymOrContest = if(id > 100000) "gym" else "contest"
    private val link1 = "https://codeforces.com/contests/$id"
    private val link2 = "https://codeforces.com/$gymOrContest/$id"
    fun getLink() = if(phase != Phase.BEFORE) link2 else link1
    fun getRegistrationLink() = "https://codeforces.com/contestRegistration/$id"

    fun startTimeInMillis() = startTimeSeconds!!.toLong() * 1000L
    fun durationInMillis() = (durationSeconds.toLong()) * 1000L

    private val daysLeft = (startTimeInMillis() - getCurrentTimeInMillis()) / (24 * 3600 * 1000)
    fun within7Days() = daysLeft in Long.MIN_VALUE..6L
    fun registrationOpen() = daysLeft in 0L..1L
}