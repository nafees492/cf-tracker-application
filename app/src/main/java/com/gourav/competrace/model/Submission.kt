package com.gourav.competrace.model

data class Submission(
    val author: Author?,
    val contestId: Int?,
    val creationTimeSeconds: Int,
    val id: Int,
    val memoryConsumedBytes: Int,
    val passedTestCount: Int,
    val points: Double?,
    val pointsInfo: String?,
    val problem: Problem,
    val programmingLanguage: String,
    val relativeTimeSeconds: Int,
    val testset: String?,
    val timeConsumedMillis: Int,
    val verdict: String
) {
    fun creationTimeInMillis() = creationTimeSeconds * 1000L
    private val gymOrContest = contestId?.let { if(it > 100000) "gym" else "contest"}  ?: "contest"
    fun getLink() = "https://codeforces.com/$gymOrContest/$contestId/submission/$id"
}
