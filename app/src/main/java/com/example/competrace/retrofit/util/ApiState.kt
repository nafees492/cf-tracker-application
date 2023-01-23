package com.example.competrace.retrofit.util

import com.example.competrace.model.ApiResult
import com.example.competrace.model.ApiResultProblemSet

sealed class ApiState {
    class Success<T>(val response: ApiResult<T>) : ApiState()
    class SuccessPS(val response: ApiResultProblemSet) : ApiState()
    class Failure(val msg: Throwable) : ApiState()
    object Loading : ApiState()
    object Empty : ApiState()
}
