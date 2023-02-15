package com.gourav.competrace.problemset.model

data class ApiResultProblemSet(
    val status: String,
    val result: ResultProblemSet?,
    val comment: String?
)