package com.gourav.competrace.contests.model

import com.gourav.competrace.app_core.util.Sites
import com.gourav.competrace.app_core.util.TimeUtils

data class KontestsContest(
    private val duration: String,
    private val end_time: String,
    private val in_24_hours: String,
    private val name: String,
    private val start_time: String,
    private val status: String,
    private val url: String,
    val site: String?
) {
    private fun getId(): String = url.split('/').last()
    private fun startTimeInMillis(): Long = TimeUtils.formattedStringToUnix(start_time)
    private fun endTimeInMillis(): Long = TimeUtils.formattedStringToUnix(end_time)
    private fun durationInMillis(): Long = endTimeInMillis() - startTimeInMillis()

    private fun getWebsiteLink(): String =
        if (site == Sites.Codeforces.title) "https://codeforces.com/contests/${getId()}"
        else url

    private fun getRegistrationLink(): String? = if (site == Sites.Codeforces.title) url else null

    fun mapToCompetraceContest(): CompetraceContest = CompetraceContest(
        id = this.getId(),
        name = this.name,
        phase = this.status,
        site = this.site ?: "Competrace",
        websiteUrl = this.getWebsiteLink(),
        startTimeInMillis = this.startTimeInMillis(),
        durationInMillis = this.durationInMillis(),
        registrationUrl = this.getRegistrationLink()
    )
}