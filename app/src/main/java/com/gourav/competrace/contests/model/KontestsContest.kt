package com.gourav.competrace.contests.model

import com.gourav.competrace.app_core.util.TimeUtils
import java.sql.Time

data class KontestsContest(
    private val duration: String,
    private val end_time: String,
    private val in_24_hours: String,
    private val name: String,
    private val start_time: String,
    private val status: String,
    private val url: String,
    val site: String?
){
    private fun getId(): String = url.split('/').last()

    private fun startTimeInMillis(): Long = TimeUtils.formattedStringToUnix(start_time)
    private fun endTimeInMillis(): Long = TimeUtils.formattedStringToUnix(end_time)
    private fun durationInMillis(): Long = endTimeInMillis() - startTimeInMillis()

    private val daysLeft: Long = (startTimeInMillis() - TimeUtils.currentTimeInMillis()) / (24 * 3600 * 1000)
    private fun within7Days(): Boolean = daysLeft in Long.MIN_VALUE..6L
    private fun registrationOpen(): Boolean = if(site == "CodeForces") daysLeft in 0L..1L else false

    private fun getWebsiteLink(): String = if(site == "CodeForces") "https://codeforces.com/contests/${getId()}"
    else  url

    private fun getRegistrationLink(): String? = if(site == "CodeForces") url else null

    fun mapToCompetraceContest(): CompetraceContest = CompetraceContest(
        id = this.getId(),
        name = this.name,
        phase = this.status,
        site = this.site ?: "Competrace",
        websiteUrl = this.getWebsiteLink(),
        startTimeInMillis = this.startTimeInMillis(),
        durationInMillis = this.durationInMillis(),
        within7Days = this.within7Days(),
        registrationOpen = this.registrationOpen(),
        registrationUrl = this.getRegistrationLink()
    )
}