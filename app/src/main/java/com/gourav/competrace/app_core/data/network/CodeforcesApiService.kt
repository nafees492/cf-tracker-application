package com.gourav.competrace.app_core.data.network

import com.gourav.competrace.app_core.model.CodeforcesApiResult
import com.gourav.competrace.problemset.model.CodeforcesContest
import com.gourav.competrace.app_core.model.CodeforcesProblemSetApiResult
import com.gourav.competrace.progress.participated_contests.model.UserRatingChanges
import com.gourav.competrace.progress.user.model.User
import com.gourav.competrace.progress.user_submissions.model.Submission
import retrofit2.http.GET
import retrofit2.http.Query


interface CodeforcesApiService {

    companion object {
        const val BASE_URL = "https://codeforces.com/api/"
    }

    @GET("user.info")
    suspend fun getUserInfo(@Query("handles") handle: String): CodeforcesApiResult<User>

    @GET("contest.list")
    suspend fun getContestList(@Query("gym") gym: Boolean): CodeforcesApiResult<CodeforcesContest>

    @GET("user.status")
    suspend fun getUserSubmissions(@Query("handle") handle: String): CodeforcesApiResult<Submission>

    @GET("problemset.problems")
    suspend fun getProblemSet(): CodeforcesProblemSetApiResult

    @GET("user.rating")
    suspend fun getUserRatingChanges(@Query("handle") handle: String): CodeforcesApiResult<UserRatingChanges>
}