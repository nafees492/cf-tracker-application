package com.gourav.competrace.problemset.util

import com.gourav.competrace.app_core.model.CodeforcesProblemSetApiResult
import com.gourav.competrace.problemset.model.CodeforcesProblem
import com.gourav.competrace.problemset.model.CompetraceProblem
import com.gourav.competrace.problemset.presentation.ProblemSetViewModel

fun ProblemSetViewModel.processCodeforcesProblemSetFromAPIResult(apiResult: CodeforcesProblemSetApiResult) {
    clearProblemsFromCodeforcesDatabase()
    tagList.clear()

    val allCodeforcesProblems: ArrayList<CodeforcesProblem> =
        apiResult.result?.problems as ArrayList<CodeforcesProblem>

    addAllProblemsToCodeforcesDatabase(
        allCodeforcesProblems.map {
            it.mapToCompetraceProblem()
        }
    )

    val setOfTags = mutableSetOf<String>()
    allCodeforcesProblems.forEach { problem ->
        problem.tags?.forEach {
            setOfTags.add(it)
        }
    }
    tagList.addAll(setOfTags)
}