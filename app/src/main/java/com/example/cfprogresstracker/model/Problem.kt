package com.example.cfprogresstracker.model

data class Problem(
    val contestId: Int?,
    val index: String,
    val name: String,
    val type: String,
    val rating: Int?,
    val tags: List<String>?,
    val points: Double?
)
