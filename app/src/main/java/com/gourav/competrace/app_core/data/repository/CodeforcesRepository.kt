package com.gourav.competrace.app_core.data.repository


import com.gourav.competrace.app_core.data.network.CodeforcesApiService
import com.gourav.competrace.app_core.model.CodeforcesApiResult
import com.gourav.competrace.contests.model.CodeforcesContest
import com.gourav.competrace.problemset.model.CodeforcesApiResultProblemSet
import com.gourav.competrace.progress.participated_contests.model.UserRatingChanges
import com.gourav.competrace.progress.user.model.User
import com.gourav.competrace.progress.user_submissions.model.Submission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CodeforcesRepository @Inject constructor(private val codeforcesApiService: CodeforcesApiService) {

    fun getUserInfo(handle: String): Flow<CodeforcesApiResult<User>> = flow {
        emit(codeforcesApiService.getUserInfo(handle = handle))
    }.flowOn(Dispatchers.IO)

    fun getContestList(gym: Boolean): Flow<CodeforcesApiResult<CodeforcesContest>> = flow {
        emit(codeforcesApiService.getContestList(gym = gym))
    }.flowOn(Dispatchers.IO)

    fun getUserSubmissions(handle: String): Flow<CodeforcesApiResult<Submission>> = flow {
        emit(codeforcesApiService.getUserSubmissions(handle = handle))
    }.flowOn(Dispatchers.IO)

    fun getProblemSet(): Flow<CodeforcesApiResultProblemSet> = flow {
        emit(codeforcesApiService.getProblemSet())
    }.flowOn(Dispatchers.IO)

    fun getUserRatingChanges(handle: String): Flow<CodeforcesApiResult<UserRatingChanges>> = flow {
        emit(codeforcesApiService.getUserRatingChanges(handle = handle))
    }.flowOn(Dispatchers.IO)
}