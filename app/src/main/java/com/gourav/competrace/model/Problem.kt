package com.gourav.competrace.model

data class Problem(
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
    fun getLinkViaProblemSet() = "https://codeforces.com/problemset/problem/$contestId/$index"
    fun getLinkViaContest() = "https://codeforces.com/contest/$contestId/problem/$index"
}
