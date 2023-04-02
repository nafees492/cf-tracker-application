package com.gourav.competrace.app_core.util

import androidx.annotation.DrawableRes
import com.gourav.competrace.R

enum class Sites(
    val title: String,
    @DrawableRes val iconId: Int,
    val isContestSite: Boolean,
    val isProblemSetSite: Boolean,
    val isUserSite: Boolean
) {
    Codeforces(
        title = "CodeForces",
        iconId = R.drawable.logo_codeforces_white_96,
        true, true, true
    ),
    CodeChef(
        title = "CodeChef",
        iconId = R.drawable.logo_codechef_white_96,
        true, false, false
    ),
    AtCoder(
        title = "AtCoder",
        iconId = R.drawable.logo_atcoder_white_96,
        true, false, false
    ),
    LeetCode(
        title = "LeetCode",
        iconId = R.drawable.logo_leetcode_white_96,
        true, false, false
    ),
    Competrace(
        title = "Competrace",
        iconId = R.drawable.competrace_96,
        false, false, false
    );

    companion object {
        fun getSite(title: String) = values().find { it.title == title } ?: Competrace
    }
}