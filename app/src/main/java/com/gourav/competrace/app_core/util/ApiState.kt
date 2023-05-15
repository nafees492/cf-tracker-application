package com.gourav.competrace.app_core.util


sealed class ApiState {
    object Loading : ApiState()
    data class Failure(val message: UiText) : ApiState()
    object Success : ApiState()
}
