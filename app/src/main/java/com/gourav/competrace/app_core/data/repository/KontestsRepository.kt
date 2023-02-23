package com.gourav.competrace.app_core.data.repository

import com.gourav.competrace.app_core.data.network.KontestsApiService
import com.gourav.competrace.contests.model.KontestsContest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class KontestsRepository @Inject constructor(private val kontestsApiService: KontestsApiService) {

    fun getAllContests(): Flow<List<KontestsContest>> = flow {
        emit(kontestsApiService.getAllContests())
    }.flowOn(Dispatchers.IO)

}