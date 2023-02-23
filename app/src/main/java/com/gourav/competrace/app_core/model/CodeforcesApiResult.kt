package com.gourav.competrace.app_core.model

data class CodeforcesApiResult<T>(
    val status: String,
    val result: List<T>?,
    val comment: String?
)