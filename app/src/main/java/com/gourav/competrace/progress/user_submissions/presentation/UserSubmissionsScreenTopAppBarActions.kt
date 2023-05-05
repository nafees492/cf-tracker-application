package com.gourav.competrace.progress.user_submissions.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceBadgeIconButton
import com.gourav.competrace.app_core.ui.components.CompetraceFilterIconButton
import com.gourav.competrace.app_core.ui.components.CompetraceIconButton
import com.gourav.competrace.app_core.util.UiText
import com.gourav.competrace.app_core.util.UserSubmissionFilter

@ExperimentalAnimationApi
@Composable
fun RowScope.UserSubmissionsScreenActions(
    onClickSearch: () -> Unit,
    badgeConditionForSearch: Boolean
) {
    CompetraceBadgeIconButton(
        badgeCondition = badgeConditionForSearch,
        iconId = R.drawable.ic_search_24px,
        onClick = onClickSearch
    )
}


