package com.gourav.competrace.problemset.util


import com.gourav.competrace.app_core.model.CodeforcesApiResult
import com.gourav.competrace.problemset.model.CodeforcesContest
import com.gourav.competrace.problemset.presentation.ProblemSetViewModel
import com.gourav.competrace.utils.ContestRatedCategories

fun ProblemSetViewModel.processCodeforcesContestFromAPIResult(
    apiResult: CodeforcesApiResult<CodeforcesContest>
) {
    val codeforcesContestList = apiResult.result?.map {
        it.mapToCompetraceContest()
    }

    codeforcesContestList?.forEach { contest ->
        contest.ratedCategories.clear()
        ContestRatedCategories.values().forEach {
            if (contest.name.contains(it.value)) contest.ratedCategories.add(it)
        }

        addContestToContestListById(codeforcesContest = contest)
    }
}