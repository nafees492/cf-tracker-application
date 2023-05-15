package com.gourav.competrace.problemset.model

import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.contests.model.CompetraceContest

data class ProblemSetScreenState(
    val selectedIndex: Int = 0,
    val apiState: ApiState = ApiState.Loading,
    val isTagsVisible: Boolean = true,
    val allTags: List<String> = emptyList(),
    val selectedTags: Set<String> = emptySet(),
    val ratingRangeValue: IntRange = 800..3500,
    val problems: List<CompetraceProblem> = emptyList(),
    val codeforcesContests: Map<Any, CompetraceContest> = emptyMap(),
){
    fun getContestName(problem: CompetraceProblem) =
        codeforcesContests[problem.contestId ?: 0]?.name
}
