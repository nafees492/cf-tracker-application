package com.gourav.competrace.app_core.data.network

import com.gourav.competrace.contests.model.KontestsContest
import retrofit2.http.GET

interface KontestsApiService {
    companion object {
        const val BASE_URL = "https://kontests.net/api/"
    }

    @GET("v1/all")
    suspend fun getAllContests(): List<KontestsContest>
}