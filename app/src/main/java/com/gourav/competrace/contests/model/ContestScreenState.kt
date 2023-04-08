package com.gourav.competrace.contests.model

import com.gourav.competrace.app_core.util.ApiState

data class ContestScreenState(
    val selectedIndex: Int = 0,
    val apiState: ApiState = ApiState.Loading,
    val ongoingContests: List<CompetraceContest> = emptyList(),
    val next7DaysContests: List<CompetraceContest> = emptyList(),
    val after7DaysContests: List<CompetraceContest> = emptyList(),
    val notificationContestIds: Set<String> = emptySet()
)