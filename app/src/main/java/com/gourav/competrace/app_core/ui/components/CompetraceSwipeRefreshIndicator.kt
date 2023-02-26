package com.gourav.competrace.app_core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState


val CompetraceSwipeRefreshIndicator:
        @Composable (state: SwipeRefreshState, refreshTrigger: Dp) -> Unit =
    { state, refreshTrigger ->
        SwipeRefreshIndicator(
            state = state, refreshTriggerDistance = refreshTrigger,
            backgroundColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inversePrimary,
            elevation = 6.dp
        )
    }