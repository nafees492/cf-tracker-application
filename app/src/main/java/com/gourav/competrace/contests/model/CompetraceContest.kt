package com.gourav.competrace.contests.model

import android.content.Context
import com.gourav.competrace.contests.util.addEventToCalendar
import com.gourav.competrace.utils.ContestRatedCategories

data class CompetraceContest(
    val id: Any,
    val name: String,
    val phase: String,
    val websiteUrl: String,
    val startTimeInMillis: Long,
    val durationInMillis: Long,
    val within7Days: Boolean,
    val registrationOpen: Boolean? = null,
    val registrationUrl: String? = null,
    val site: String? = null,
) {
    val endTimeInMillis = startTimeInMillis + durationInMillis
    val ratedCategories = arrayListOf<ContestRatedCategories>()

    var newRating: Int = 0
    var ratingChange: Int = 0
    var rank: Int = 0

    fun addToCalender(context: Context) {
        addEventToCalendar(
            context = context,
            title = name,
            startTime = startTimeInMillis,
            endTime = endTimeInMillis,
            location = websiteUrl,
            description = "Add Contest to Calender"
        )
    }
}

