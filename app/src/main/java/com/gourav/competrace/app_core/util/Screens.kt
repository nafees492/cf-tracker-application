package com.gourav.competrace.app_core.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gourav.competrace.R

enum class Screens(
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int?,
    val route: String,
    val isHomeScreen: Boolean
) {
    ContestsScreen(
        R.string.contests,
        R.drawable.ic_round_leaderboard_24,
        "home/contests",
        true
    ),
    ProblemSetScreen(
        R.string.problem_set,
        R.drawable.ic_round_assignment_24,
        "home/problemset",
        true
    ),
    ProgressScreen(
        R.string.progress,
        R.drawable.ic_round_insights_24,
        "home/progress",
        true
    ),
    ParticipatedContestsScreen(
        R.string.participated_contests,
        null,
        "home/progress/participated-contests",
        false
    ),
    UserSubmissionsScreen(
        R.string.your_submissions,
        null,
        "home/progress/user-submission",
        false
    ),
    SettingsScreen(
        R.string.settings,
        R.drawable.ic_baseline_settings_24px,
        "home/settings",
        false
    );
}