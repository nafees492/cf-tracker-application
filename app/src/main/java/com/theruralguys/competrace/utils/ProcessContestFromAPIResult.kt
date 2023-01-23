package com.theruralguys.competrace.utils

import com.theruralguys.competrace.model.Contest
import com.theruralguys.competrace.retrofit.util.ApiState
import com.theruralguys.competrace.viewmodel.MainViewModel
import kotlin.math.abs

fun processContestFromAPIResult(apiResult: ApiState.Success<*>, mainViewModel: MainViewModel) {

    mainViewModel.contestListById.clear()
    mainViewModel.contestListsByPhase.forEach {
        it.value.clear()
    }
    val contestList = apiResult.response.result as List<Contest>

    contestList.forEach { contest ->
        mainViewModel.contestListsByPhase[contest.phase]?.add(contest)
        mainViewModel.contestListById[contest.id] = contest

        contest.rated.clear()
        ContestRated.contestRatedList.forEach { if(contest.name.contains(it)) contest.rated.add(it) }
    }

    mainViewModel.contestListBeforeByPhase.forEach {
        it.value.clear()
    }
    val currentDate = getTodaysDate()
    mainViewModel.contestListsByPhase[Phase.BEFORE]?.forEach { contest ->
        val contestDate = contest.getContestDate()
        when ((abs(contestDate.time - currentDate.time) / (24 * 3600 * 1000))) {
            in 0L..6L -> mainViewModel.contestListBeforeByPhase[Phase.WITHIN_7DAYS]!!.add(
                contest
            )
            else -> mainViewModel.contestListBeforeByPhase[Phase.AFTER_7DAYS]!!.add(
                contest
            )
        }
    }
}