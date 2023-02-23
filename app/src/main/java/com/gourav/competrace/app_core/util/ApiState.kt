package com.gourav.competrace.app_core.util


sealed class ApiState {
    object Empty : ApiState()
    object Loading : ApiState()
    object Failure : ApiState()
    object Success : ApiState()
}
