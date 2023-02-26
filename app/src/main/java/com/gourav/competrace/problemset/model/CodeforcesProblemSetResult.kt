package com.gourav.competrace.problemset.model

data class CodeforcesProblemSetResult(
    val problems: List<CodeforcesProblem>,
    val problemStatistics: List<CodeforcesProblemStatistic>,
)