package com.gourav.competrace.contests.util


import com.gourav.competrace.app_core.model.CodeforcesApiResult
import com.gourav.competrace.contests.model.CodeforcesContest
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.problemset.presentation.ProblemSetViewModel
import com.gourav.competrace.utils.ContestRatedCategories

fun ProblemSetViewModel.processCodeforcesContestFromAPIResult(
    apiResult: CodeforcesApiResult<CodeforcesContest>
) {
    val codeforcesContestList = apiResult.result?.map {
        CompetraceContest(
            id = it.id,
            name = it.name,
            phase =  it.phase,
            websiteUrl = it.getLink(),
            startTimeInMillis = it.startTimeInMillis(),
            durationInMillis = it.durationInMillis(),
            within7Days = it.within7Days(),
            registrationOpen = it.registrationOpen(),
            registrationUrl = it.getRegistrationLink()
        )
    }

    codeforcesContestList?.forEach { contest ->
        contest.ratedCategories.clear()
        ContestRatedCategories.values().forEach {
            if (contest.name.contains(it.value)) contest.ratedCategories.add(it)
        }

        addContestToContestListById(codeforcesContest = contest)
    }
}