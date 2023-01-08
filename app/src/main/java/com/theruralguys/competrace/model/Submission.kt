package com.theruralguys.competrace.model

data class Submission(
    val id: Int,
    val contestId: Int?,
    val creationTimeSeconds: Int,
    val problem: Problem,
    val programmingLanguage: String,
    val verdict: String?,
    val passedTestCount: Int,
    val timeConsumedMillis: Int,
    val memoryConsumedBytes: Int
) {
    fun creationTimeInMillis() = creationTimeSeconds * 1000L
}
