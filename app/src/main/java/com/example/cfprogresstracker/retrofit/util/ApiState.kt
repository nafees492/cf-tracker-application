package com.example.cfprogresstracker.retrofit.util

import com.example.cfprogresstracker.model.ApiResult
import com.example.cfprogresstracker.model.ApiResultProblemSet

sealed class ApiState {
    class Success<T>(val response: ApiResult<T>) : ApiState()
    class SuccessPS(val response: ApiResultProblemSet) : ApiState()
    class Failure(val msg: Throwable) : ApiState()
    object Loading : ApiState()
    object Empty : ApiState()
}
