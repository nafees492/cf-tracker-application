package com.gourav.competrace.problemset.model

import com.gourav.competrace.contests.model.CompetraceContest

data class CompetraceProblem(
    val index: String,
    val name: String,
    val contestId: Int?,
    val rating: Int?,
    val tags: List<String>?,
    val websiteUrl: String?
) {
    var hasVerdictOK: Boolean = false
}
