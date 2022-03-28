package com.example.cfprogresstracker.retrofit.network

import com.example.cfprogresstracker.model.*
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    companion object {
        const val BASE_URL = "https://codeforces.com/api/"
    }

    @GET("user.info")
    suspend fun getUserInfo(@Query("handles") handle: String): ApiResult<User>

    @GET("contest.list")
    suspend fun getContestList(): ApiResult<Contest>

    @GET("user.status")
    suspend fun getUserSubmissions(@Query("handle") userHandle: String): ApiResult<Submission>

    @GET("problemset.problems")
    suspend fun getProblemSet(): ApiResultProblemSet
}