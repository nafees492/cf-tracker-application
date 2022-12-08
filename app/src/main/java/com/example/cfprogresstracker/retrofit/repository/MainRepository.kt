package com.example.cfprogresstracker.retrofit.repository

import com.example.cfprogresstracker.model.*
import com.example.cfprogresstracker.retrofit.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiService: ApiService) {

    fun getUserInfo(handle: String): Flow<ApiResult<User>> = flow {
        emit(apiService.getUserInfo(handle = handle))
    }.flowOn(Dispatchers.IO)

    fun getContestList(): Flow<ApiResult<Contest>> = flow {
        emit(apiService.getContestList())
    }.flowOn(Dispatchers.IO)

    fun getUserSubmissions(userHandle: String): Flow<ApiResult<Submission>> = flow {
        emit(apiService.getUserSubmissions(userHandle = userHandle))
    }.flowOn(Dispatchers.IO)

    fun getProblemSet(): Flow<ApiResultProblemSet> = flow {
        emit(apiService.getProblemSet())
    }.flowOn(Dispatchers.IO)

    fun getUserRatingChanges(userHandle: String): Flow<ApiResult<UserRatingChanges>> = flow {
        emit(apiService.getUserRatingChanges(userHandle))
    }.flowOn(Dispatchers.IO)
}