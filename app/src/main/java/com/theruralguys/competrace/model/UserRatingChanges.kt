package com.theruralguys.competrace.model

data class UserRatingChanges(
    val contestId: Int,
    val contestName: String,
    val handle: String,
    val newRating: Int,
    val oldRating: Int,
    val rank: Int,
    val ratingUpdateTimeSeconds: Int
)