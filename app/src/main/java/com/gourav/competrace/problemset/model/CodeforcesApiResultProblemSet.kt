package com.gourav.competrace.problemset.model

data class CodeforcesApiResultProblemSet(
    val status: String,
    val result: CodeforcesResultProblemSet?,
    val comment: String?
)