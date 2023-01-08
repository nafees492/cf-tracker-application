package com.theruralguys.competrace.retrofit.network

import com.theruralguys.competrace.model.*
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

    @GET("user.rating")
    suspend fun getUserRatingChanges(@Query("handle") userHandle: String): ApiResult<UserRatingChanges>
}