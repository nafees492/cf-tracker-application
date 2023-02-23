package com.gourav.competrace.contests.model

import com.gourav.competrace.app_core.util.formattedStringToUnix
import com.gourav.competrace.app_core.util.getCurrentTimeInMillis

data class KontestsContest(
    val duration: String,
    val end_time: String,
    val in_24_hours: String,
    val name: String,
    val start_time: String,
    val status: String,
    val url: String,
    val site: String?
){
    private fun getId(): String = url.split('/').last()

    fun startTimeInMillis(): Long = formattedStringToUnix(start_time)
    private fun endTimeInMillis(): Long = formattedStringToUnix(end_time)
    fun durationInMillis(): Long = endTimeInMillis() - startTimeInMillis()

    private val daysLeft: Long = (startTimeInMillis() - getCurrentTimeInMillis()) / (24 * 3600 * 1000)
    fun within7Days(): Boolean = daysLeft in Long.MIN_VALUE..6L
    fun registrationOpen(): Boolean? = if(site == "CodeForces") daysLeft in 0L..1L else null

    fun getWebsiteLink(): String = if(site == "CodeForces") "https://codeforces.com/contests/${getId()}"
    else  url

    fun getRegistrationLink(): String? = if(site == "CodeForces") url else null

}