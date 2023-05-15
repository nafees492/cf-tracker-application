package com.gourav.competrace.app_core.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gourav.competrace.R

enum class Screens(
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int?,
    val screenName: String,
    val route: String,
    val isHomeScreen: Boolean,
) {
    ContestsScreen(
        titleId = R.string.contests,
        iconId = R.drawable.ic_round_leaderboard_24,
        screenName = "Contests",
        route = "home/contests",
        isHomeScreen = true
    ),
    ProblemSetScreen(
        titleId = R.string.problem_set,
        iconId = R.drawable.ic_round_assignment_24,
        screenName = "Problem Set",
        route = "home/problemset",
        isHomeScreen = true
    ),
    ProgressScreen(
        titleId = R.string.progress,
        iconId = R.drawable.ic_round_insights_24,
        screenName = "Progress",
        route = "home/progress",
        isHomeScreen = true
    ),
    ParticipatedContestsScreen(
        titleId = R.string.participated_contests,
        iconId = null,
        screenName = "Participated Contests",
        route = "home/progress/participated-contests",
        isHomeScreen = false
    ),
    UserSubmissionsScreen(
        titleId = R.string.your_submissions,
        iconId = null,
        screenName = "User Submission",
        route = "home/progress/user-submission",
        isHomeScreen = false
    ),
    SettingsScreen(
        titleId = R.string.settings,
        iconId = R.drawable.ic_baseline_settings_24px,
        screenName = "Settings",
        route = "home/settings",
        isHomeScreen = false
    );
}