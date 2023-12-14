package com.gourav.competrace.app_core.util

import androidx.annotation.DrawableRes
import com.gourav.competrace.R

enum class Sites(
    val title: String,
    @DrawableRes val iconId: Int,
    val isContestSite: Boolean,
    val isProblemSetSite: Boolean,
    val isUserSite: Boolean,
    val contestsUrl: String? = null,
    val problemSetUrl: String? = null
) {
    Codeforces(
        title = "CodeForces",
        iconId = R.drawable.logo_codeforces_white_96,
        true, true, true,
        contestsUrl = "https://codeforces.com/contests",
        problemSetUrl = "https://codeforces.com/problemset"
    ),
   /* CodeChef(
        title = "CodeChef",
        iconId = R.drawable.logo_codechef_white_96,
        true, false, false,
        contestsUrl = "https://www.codechef.com/contests"
    ),
    AtCoder(
        title = "AtCoder",
        iconId = R.drawable.logo_atcoder_white_96,
        true, false, false,
        contestsUrl = "https://atcoder.jp/contests"
    ),
    LeetCode(
        title = "LeetCode",
        iconId = R.drawable.logo_leetcode_white_96,
        true, false, false,
        contestsUrl = "https://leetcode.com/contest"
    ),*/
    Competrace(
        title = "Competrace",
        iconId = R.drawable.competrace_notification_icon,
        false, false, false
    );

    companion object {
        fun getSite(title: String) = values().find { it.title == title } ?: Competrace
    }
}