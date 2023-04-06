package com.gourav.competrace.app_core.data

import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.problemset.model.CompetraceProblem
import com.gourav.competrace.progress.user.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class CodeforcesDatabase private constructor() {

    val currentUserFlow: MutableStateFlow<User?> = MutableStateFlow(null)
    fun setUser(user: User){
        currentUserFlow.update { user }
    }

    val codeforcesContestListByIdFlow = MutableStateFlow (mapOf<Any, CompetraceContest>())
    fun addContestToContestListById(contest: CompetraceContest){
        codeforcesContestListByIdFlow.update {
            it + (contest.id to contest)
        }
    }

    val allProblemsFlow = MutableStateFlow(listOf<CompetraceProblem>())
    fun addAllProblems(problems: List<CompetraceProblem>){
        allProblemsFlow.update { it + problems }
    }
    fun clearProblems(){
        allProblemsFlow.update { emptyList() }
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