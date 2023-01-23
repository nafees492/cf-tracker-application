package com.example.competrace.model

data class ApiResult<T>(
    val status: String,
    val result: List<T>?,
    val comment: String?
)