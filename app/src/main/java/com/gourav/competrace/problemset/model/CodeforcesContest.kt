package com.gourav.competrace.problemset.model

import com.gourav.competrace.app_core.util.Sites
import com.gourav.competrace.contests.model.CompetraceContest

data class CodeforcesContest(
    private val id: Int,
    private val name: String,
    private val phase: String,
    private val type: String?,
    private val frozen: Boolean?,
    private val durationSeconds: Int?,
    private val startTimeSeconds: Int?,
    private val relativeTimeSeconds: Int?,
    private val difficulty: Int?,
    private val kind: String?,
    private val preparedBy: String?,
    private val websiteUrl: String?,
    private val description: String?,
    private val icpcRegion: String?,
    private val country: String?,
    private val city: String?,
    private val season: String?
) {
    private val gymOrContest = if(id > 100000) "gym" else "contest"
    private fun getLink() = "https://codeforces.com/$gymOrContest/$id"
    private fun getRegistrationLink() = "https://codeforces.com/contestRegistration/$id"

    private fun startTimeInMillis() = startTimeSeconds?.toLong()?.times(1000L) ?: 0
    private fun durationInMillis() = durationSeconds?.toLong()?.times(1000L) ?: 0

    fun mapToCompetraceContest(): CompetraceContest = CompetraceContest(
        id = this.id,
        name = this.name,
        phase =  this.phase,
        websiteUrl = this.getLink(),
        startTimeInMillis = this.startTimeInMillis(),
        durationInMillis = this.durationInMillis(),
        site = Sites.Codeforces.title,
        registrationUrl = this.getRegistrationLink()
    )
}