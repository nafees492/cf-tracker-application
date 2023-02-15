package com.gourav.competrace.app_core.util


import com.gourav.competrace.app_core.model.ApiResult
import com.gourav.competrace.problemset.model.ApiResultProblemSet

sealed class ApiState {
    class Success<T>(val response: ApiResult<T>) : ApiState()
    class SuccessPS(val response: ApiResultProblemSet) : ApiState()
    class Failure(val msg: Throwable) : ApiState()
    object Loading : ApiState()
    object Empty : ApiState()
}
