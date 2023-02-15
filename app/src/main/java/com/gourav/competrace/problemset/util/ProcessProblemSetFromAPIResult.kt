package com.gourav.competrace.utils

import com.gourav.competrace.problemset.model.ApiResultProblemSet
import com.gourav.competrace.problemset.model.Problem
import com.gourav.competrace.app_core.MainViewModel

fun processProblemSetFromAPIResult(apiResult: ApiResultProblemSet, mainViewModel: MainViewModel) {
    mainViewModel.allProblems.clear()
    mainViewModel.tagList.clear()

    val allProblems: ArrayList<Problem> =
        apiResult.result!!.problems as ArrayList<Problem>
    mainViewModel.allProblems.addAll(allProblems)

    val setOfTags = mutableSetOf<String>()
    allProblems.forEach { problem ->
        problem.tags?.forEach {
            setOfTags.add(it)
        }
    }
    mainViewModel.tagList.addAll(setOfTags)
}