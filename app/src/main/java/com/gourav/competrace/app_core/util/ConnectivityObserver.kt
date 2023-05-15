package com.gourav.competrace.app_core.util

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Lost
    }
}