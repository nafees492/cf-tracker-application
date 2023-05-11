package com.gourav.competrace.problemset.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceBadgeIconButton
import com.gourav.competrace.app_core.ui.components.CompetraceIconButton

@ExperimentalAnimationApi
@Composable
fun ProblemSetScreenActions(
    onClickSearch: () -> Unit,
    openSettings: () -> Unit,
    openSite: () -> Unit,
    badgeConditionForSearch: Boolean,
) {

    CompetraceIconButton(
        iconId = R.drawable.ic_baseline_settings_24px,
        onClick = openSettings,
        contentDescription = stringResource(id = R.string.settings)
    )

    CompetraceBadgeIconButton(
        badgeCondition = badgeConditionForSearch,
        iconId = R.drawable.ic_search_24px,
        onClick = onClickSearch,
        contentDescription = stringResource(id = R.string.cd_search_icon)
    )

    CompetraceIconButton(
        iconId = R.drawable.ic_open_in_browser_24px,
        onClick = openSite,
        contentDescription = stringResource(id = R.string.open_in_browser)
    )
}


