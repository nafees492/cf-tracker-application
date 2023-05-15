package com.gourav.competrace.app_core.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BoxScope.CompetracePullRefreshIndicator(
    refreshing: Boolean,
    state: PullRefreshState
){
    PullRefreshIndicator(
        refreshing = refreshing,
        state = state,
        backgroundColor = MaterialTheme.colorScheme.inverseSurface,
        contentColor = MaterialTheme.colorScheme.inversePrimary,
        modifier = Modifier.align(Alignment.TopCenter)
    )
}