package com.theruralguys.competrace.utils

import com.theruralguys.competrace.model.Problem
import com.theruralguys.competrace.model.Submission
import com.theruralguys.competrace.retrofit.util.ApiState
import com.theruralguys.competrace.viewmodel.MainViewModel

fun processSubmittedProblemFromAPI(mainViewModel: MainViewModel, apiResult: ApiState.Success<*>){
    mainViewModel.submittedProblems.clear()
    mainViewModel.incorrectProblems.clear()
    mainViewModel.correctProblems.clear()

    val problemNameMapWithSubmissions =
        mutableMapOf<String, ArrayList<Submission>>()
    val problemNameMapWithProblem = mutableMapOf<String, Problem>()
    val problemNameWithVerdictOK: MutableSet<String> = mutableSetOf()

    val submissions: ArrayList<Submission> =
        apiResult.response.result as ArrayList<Submission>

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

    for (problem in problemNameMapWithProblem) {
        Pair(problem.value, problemNameMapWithSubmissions[problem.key]!!).let {
            mainViewModel.submittedProblems.add(it)
            if (problem.value.hasVerdictOK) mainViewModel.correctProblems.add(it)
            else mainViewModel.incorrectProblems.add(it)
        }
    }
}