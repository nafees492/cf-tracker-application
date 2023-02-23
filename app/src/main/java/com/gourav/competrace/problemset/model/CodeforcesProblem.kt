package com.gourav.competrace.problemset.model

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
    fun getLinkViaProblemSet() = "https://codeforces.com/problemset/problem/$contestId/$index"

    fun getLinkViaContest() = "https://codeforces.com/$gymOrContest/$contestId/problem/$index"
}
