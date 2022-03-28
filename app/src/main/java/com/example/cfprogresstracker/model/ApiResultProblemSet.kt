package com.example.cfprogresstracker.model

data class ApiResultProblemSet(
    val status: String,
    val result: ResultProblemSet?,
    val comment: String?
)