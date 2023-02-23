package com.gourav.competrace.problemset.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceFilterIconButton
import com.gourav.competrace.app_core.ui.components.CompetraceIconButton

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun ProblemSetScreenActions(
    onClickSearch: () -> Unit,
    onClickSettings: () -> Unit,
    onClickFilterIcon: () -> Unit,
    isToolbarExpanded: Boolean,
    ratingRange: ClosedRange<Int>
) {

    CompetraceIconButton(
        iconId = R.drawable.ic_search_24px,
        onClick = onClickSearch,
        contentDescription = "Search in Problem set"
    )

    CompetraceIconButton(
        iconId = R.drawable.ic_baseline_settings_24px,
        onClick = onClickSettings,
        contentDescription = "Settings"
    )

    CompetraceFilterIconButton(
        isActive = isToolbarExpanded,
        badgeCondition = ratingRange != 800..3500,
        onClick = onClickFilterIcon
    )
}


