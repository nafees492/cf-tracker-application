package com.gourav.competrace.utils

import com.gourav.competrace.app_core.MainViewModel
import com.gourav.competrace.app_core.model.ApiResult
import com.gourav.competrace.problemset.model.Problem
import com.gourav.competrace.progress.user_submissions.model.Submission

fun processUserSubmissionsFromAPIResult(apiResult: ApiResult<Submission>, mainViewModel: MainViewModel){
    mainViewModel.submittedProblems.clear()
    mainViewModel.incorrectProblems.clear()
    mainViewModel.correctProblems.clear()

    val problemNameMapWithSubmissions =
        mutableMapOf<String, ArrayList<Submission>>()
    val problemNameMapWithProblem = mutableMapOf<String, Problem>()
    val problemNameWithVerdictOK: MutableSet<String> = mutableSetOf()

    val submissions: ArrayList<Submission> = apiResult.result as ArrayList<Submission>

    submissions.forEach {
        if (it.verdict == Verdict.OK) problemNameWithVerdictOK.add(it.problem.name)

        problemNameMapWithProblem[it.problem.name] = it.problem

        if (problemNameMapWithSubmissions[it.problem.name].isNullOrEmpty()) {
            problemNameMapWithSubmissions[it.problem.name] = arrayListOf(it)
        } else {
            problemNameMapWithSubmissions[it.problem.name]?.add(it)
        }
    }

    problemNameWithVerdictOK.forEach {
        problemNameMapWithProblem[it]?.hasVerdictOK = true
    }

    problemNameMapWithProblem.forEach { problem ->
        Pair(problem.value, problemNameMapWithSubmissions[problem.key]!!).let {
            mainViewModel.submittedProblems.add(it)
            if (problem.value.hasVerdictOK) mainViewModel.correctProblems.add(it)
            else mainViewModel.incorrectProblems.add(it)
        }
    }
}