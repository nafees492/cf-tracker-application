package com.gourav.competrace.progress.user_submissions.util

import com.gourav.competrace.app_core.model.CodeforcesApiResult
import com.gourav.competrace.problemset.model.CodeforcesProblem
import com.gourav.competrace.progress.user_submissions.model.Submission
import com.gourav.competrace.progress.user_submissions.presentation.UserSubmissionsViewModel
import com.gourav.competrace.app_core.util.Verdict

fun UserSubmissionsViewModel.processUserSubmissionsFromAPIResult(codeforcesApiResult: CodeforcesApiResult<Submission>){
    submittedProblemsFlow.value.clear()
    incorrectProblems.clear()
    correctProblems.clear()

    val problemNameMapWithSubmissions =
        mutableMapOf<String, ArrayList<Submission>>()
    val problemNameMapWithCodeforcesProblem = mutableMapOf<String, CodeforcesProblem>()
    val problemNameWithVerdictOK: MutableSet<String> = mutableSetOf()

    val submissions: ArrayList<Submission> = codeforcesApiResult.result as ArrayList<Submission>

    submissions.forEach {
        if (it.verdict == Verdict.OK) problemNameWithVerdictOK.add(it.problem.name)

        problemNameMapWithCodeforcesProblem[it.problem.name] = it.problem

        if (problemNameMapWithSubmissions[it.problem.name].isNullOrEmpty()) {
            problemNameMapWithSubmissions[it.problem.name] = arrayListOf(it)
        } else {
            problemNameMapWithSubmissions[it.problem.name]?.add(it)
        }
    }

    problemNameWithVerdictOK.forEach {
        problemNameMapWithCodeforcesProblem[it]?.hasVerdictOK = true
    }

    problemNameMapWithCodeforcesProblem.forEach { problem ->
        Pair(problem.value, problemNameMapWithSubmissions[problem.key]!!).let {
            submittedProblemsFlow.value.add(it)
            if (problem.value.hasVerdictOK) correctProblems.add(it)
            else incorrectProblems.add(it)
        }
    }
}