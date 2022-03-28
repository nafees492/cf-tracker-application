package com.example.cfprogresstracker.model

data class ApiResult<T>(
    val status: String,
    val result: List<T>?,
    val comment: String?
)