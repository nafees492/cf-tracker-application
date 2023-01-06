package com.example.cfprogresstracker.utils

import com.example.cfprogresstracker.model.Problem
import com.example.cfprogresstracker.model.Submission
import com.example.cfprogresstracker.retrofit.util.ApiState
import com.example.cfprogresstracker.viewmodel.MainViewModel

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