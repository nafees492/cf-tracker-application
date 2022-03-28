package com.example.cfprogresstracker.model

data class Contest(
    val id: Int,
    val name: String,
    val type: String,
    val phase: String,
    val frozen: Boolean,
    val durationSeconds: Int,
    val startTimeSeconds: Int?,
    val relativeTimeSeconds: Int?,
    val difficulty: Int?,
    val kind: String?,
    var hasSubmissions: Boolean = false,
    var totalCorrect: Int = 0
)