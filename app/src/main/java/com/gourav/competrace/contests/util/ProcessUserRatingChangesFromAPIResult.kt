package com.gourav.competrace.utils

import com.gourav.competrace.app_core.model.ApiResult
import com.gourav.competrace.contests.model.UserRatingChanges
import com.gourav.competrace.app_core.MainViewModel

fun processUserRatingChangesFromAPIResult(
    apiResult: ApiResult<UserRatingChanges>,
    mainViewModel: MainViewModel
){
    val userRatingChanges = apiResult.result as List<UserRatingChanges>

    userRatingChanges.forEach { userRatingChange ->
        mainViewModel.contestListsByPhase[Phase.FINISHED]!!.find {
            it.id == userRatingChange.contestId
        }?.also {
            it.isAttempted = true
            it.ratingChange =
                userRatingChange.newRating - userRatingChange.oldRating
            it.rank = userRatingChange.rank
            it.newRating =
                userRatingChange.newRating
        }
    }
}