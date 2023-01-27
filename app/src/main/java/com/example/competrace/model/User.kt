package com.example.competrace.model

import com.example.competrace.utils.RatingCategories

data class User(
    val avatar: String,
    val city: String?,
    val contribution: Int,
    val country: String?,
    val firstName: String?,
    val friendOfCount: Int,
    val handle: String,
    val lastName: String?,
    val lastOnlineTimeSeconds: Int,
    val maxRank: String?,
    val maxRating: Int?,
    val organization: String?,
    val rank: String?,
    val rating: Int?,
    val registrationTimeSeconds: Int,
    val titlePhoto: String,
    val email: String?,
    val vkId: String?,
    val openId: String?,
){
    val ratingCategory = rating?.let {
            when(it){
                in 0..799 -> RatingCategories.Unrated
                in 800..1199 -> RatingCategories.Newbie
                in 1200..1399 -> RatingCategories.Pupil
                in 1400..1599 -> RatingCategories.Specialist
                in 1600..1899 -> RatingCategories.Expert
                in 1900..2099 -> RatingCategories.CandidateMaster
                in 2100..2299 -> RatingCategories.Master
                in 2300..2399 -> RatingCategories.InternationalMaster
                in 2400..2599 -> RatingCategories.Grandmaster
                in 2600..2999 -> RatingCategories.InternationalGrandmaster
                else -> RatingCategories.LegendaryGrandmaster
            }
        } ?: RatingCategories.Unrated
}