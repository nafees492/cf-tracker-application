package com.gourav.competrace.progress.user_submissions.model

data class Author(
    val contestId: Int?,
    val ghost: Boolean?,
    val members: List<Member>?,
    val participantType: String?,
    val room: Int?,
    val startTimeSeconds: Int?,
    val teamId: Int?,
    val teamName: String?
)