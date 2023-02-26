package com.gourav.competrace.problemset.model

import com.gourav.competrace.contests.model.CompetraceContest

data class CodeforcesProblem(
    val contestId: Int?,
    val index: String,
    val name: String,
    val type: String,
    val problemsetName: String?,
    val rating: Int?,
    val tags: List<String>?,
    val points: Double?,
) {
    var hasVerdictOK: Boolean = false
    private val gymOrContest = contestId?.let { if(it > 100000) "gym" else "contest"}  ?: "contest"
    private fun getLinkViaProblemSet() = "https://codeforces.com/problemset/problem/$contestId/$index"

    fun getLinkViaContest() = "https://codeforces.com/$gymOrContest/$contestId/problem/$index"

    fun mapToCompetraceProblem(): CompetraceProblem = CompetraceProblem(
        index = this.index,
        name = this.name,
        contestId = this.contestId,
        rating = this.rating,
        tags = this.tags,
        websiteUrl = getLinkViaProblemSet()
    )
}
