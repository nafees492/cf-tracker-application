package com.gourav.competrace.model

data class ApiResultProblemSet(
    val status: String,
    val result: ResultProblemSet?,
    val comment: String?
)