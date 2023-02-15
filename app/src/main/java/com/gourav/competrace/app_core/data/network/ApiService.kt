package com.gourav.competrace.app_core.data.network

import com.gourav.competrace.app_core.model.ApiResult
import com.gourav.competrace.contests.model.Contest
import com.gourav.competrace.contests.model.UserRatingChanges
import com.gourav.competrace.problemset.model.ApiResultProblemSet
import com.gourav.competrace.progress.user.model.User
import com.gourav.competrace.progress.user_submissions.model.Submission
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    companion object {
        const val BASE_URL = "https://codeforces.com/api/"
    }

    @GET("user.info")
    suspend fun getUserInfo(@Query("handles") handle: String): ApiResult<User>

    @GET("contest.list")
    suspend fun getContestList(@Query("gym") gym: Boolean): ApiResult<Contest>

    @GET("user.status")
    suspend fun getUserSubmissions(@Query("handle") handle: String): ApiResult<Submission>

    @GET("problemset.problems")
    suspend fun getProblemSet(): ApiResultProblemSet

    @GET("user.rating")
    suspend fun getUserRatingChanges(@Query("handle") handle: String): ApiResult<UserRatingChanges>
}