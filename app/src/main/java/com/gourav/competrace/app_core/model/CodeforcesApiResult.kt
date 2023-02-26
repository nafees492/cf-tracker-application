package com.gourav.competrace.app_core.model

import com.gourav.competrace.problemset.model.CodeforcesProblemSetResult

data class CodeforcesApiResult<T>(
    val status: String,
    val result: List<T>?,
    val comment: String?
)

data class CodeforcesProblemSetApiResult(
    val status: String,
    val result: CodeforcesProblemSetResult?,
    val comment: String?
)