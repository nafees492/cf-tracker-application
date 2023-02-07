package com.gourav.competrace.retrofit.repository


import com.gourav.competrace.model.*
import com.gourav.competrace.retrofit.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiService: ApiService) {

    fun getUserInfo(handle: String): Flow<ApiResult<User>> = flow {
        emit(apiService.getUserInfo(handle = handle))
    }.flowOn(Dispatchers.IO)

    fun getContestList(gym: Boolean): Flow<ApiResult<Contest>> = flow {
        emit(apiService.getContestList(gym = gym))
    }.flowOn(Dispatchers.IO)

    fun getUserSubmissions(handle: String): Flow<ApiResult<Submission>> = flow {
        emit(apiService.getUserSubmissions(handle = handle))
    }.flowOn(Dispatchers.IO)

    fun getProblemSet(): Flow<ApiResultProblemSet> = flow {
        emit(apiService.getProblemSet())
    }.flowOn(Dispatchers.IO)

    fun getUserRatingChanges(handle: String): Flow<ApiResult<UserRatingChanges>> = flow {
        emit(apiService.getUserRatingChanges(handle = handle))
    }.flowOn(Dispatchers.IO)
}