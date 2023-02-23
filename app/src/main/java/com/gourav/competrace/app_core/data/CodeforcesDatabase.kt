package com.gourav.competrace.app_core.data

import com.gourav.competrace.app_core.util.tempUser
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.problemset.model.CodeforcesProblem
import com.gourav.competrace.progress.user.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class CodeforcesDatabase private constructor() {

    val currentUserFlow = MutableStateFlow(tempUser)
    fun setUser(user: User){
        currentUserFlow.update { user }
    }

    val codeforcesContestListByIdFlow = MutableStateFlow (mutableMapOf<Any, CompetraceContest>())
    fun addContestToContestListById(codeforcesContest: CompetraceContest){
        codeforcesContestListByIdFlow.value[codeforcesContest.id] = codeforcesContest
    }

    val allProblemsFlow = MutableStateFlow(arrayListOf<CodeforcesProblem>())
    fun addProblem(codeforcesProblem: CodeforcesProblem){
        allProblemsFlow.value.add(codeforcesProblem)
    }
    fun addAllProblems(codeforcesProblems: List<CodeforcesProblem>){
        allProblemsFlow.value.addAll(codeforcesProblems)
    }
    fun clearProblems(){
        allProblemsFlow.value.clear()
    }

    companion object {
        @get:Synchronized
        var instance: CodeforcesDatabase? = null
            get() {
                if (field == null) {
                    field = CodeforcesDatabase()
                }
                return field
            }
    }
}