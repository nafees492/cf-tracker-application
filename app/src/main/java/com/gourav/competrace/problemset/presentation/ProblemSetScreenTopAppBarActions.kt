package com.gourav.competrace.problemset.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceBadgeIconButton
import com.gourav.competrace.app_core.ui.components.CompetraceFilterIconButton
import com.gourav.competrace.app_core.ui.components.CompetraceIconButton
import java.util.concurrent.locks.Condition

@ExperimentalAnimationApi
@Composable
fun ProblemSetScreenActions(
    onClickSearch: () -> Unit,
    onClickSettings: () -> Unit,
    onClickFilterIcon: () -> Unit,
    badgeConditionForSearch: Boolean,
    badgeConditionForFilter: Boolean,
) {

    CompetraceBadgeIconButton(
        badgeCondition = badgeConditionForSearch,
        iconId = R.drawable.ic_search_24px,
        onClick = onClickSearch,
        contentDescription = stringResource(id = R.string.cd_search_icon)
    )

    CompetraceIconButton(
        iconId = R.drawable.ic_baseline_settings_24px,
        onClick = onClickSettings,
        contentDescription = stringResource(id = R.string.settings)
    )

    CompetraceFilterIconButton(
        isActive = false,
        badgeCondition = badgeConditionForFilter,
        onClick = onClickFilterIcon
    )
}


