package com.gourav.competrace.problemset.util

import com.gourav.competrace.problemset.model.CodeforcesApiResultProblemSet
import com.gourav.competrace.problemset.model.CodeforcesProblem
import com.gourav.competrace.problemset.presentation.ProblemSetViewModel

fun ProblemSetViewModel.processCodeforcesProblemSetFromAPIResult(apiResult: CodeforcesApiResultProblemSet) {
    clearProblemsFromCodeforcesDatabase()
    tagList.clear()

    val allCodeforcesProblems: ArrayList<CodeforcesProblem> =
        apiResult.result!!.problems as ArrayList<CodeforcesProblem>
    addAllProblemsToCodeforcesDatabase(allCodeforcesProblems)

    val setOfTags = mutableSetOf<String>()
    allCodeforcesProblems.forEach { problem ->
        problem.tags?.forEach {
            setOfTags.add(it)
        }
    }
    tagList.addAll(setOfTags)
}