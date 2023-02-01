package com.gourav.competrace.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.gourav.competrace.R

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

    NormalIconButton(
        iconId = R.drawable.ic_search_24px,
        onClick = onClickSearch,
        contentDescription = "Search in Problem set"
    )

    IconButton(onClick = onClickSettings) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_settings_24px),
            contentDescription = "Settings",
        )
    }

    IconButton(onClick = onClickFilterIcon) {
        BadgedBox(badge = { if (ratingRange != 800..3500) Badge() }) {
            AnimatedContent(
                targetState = isToolbarExpanded,
            ) {
                val iconId =
                    if (it) R.drawable.ic_expand_less_24px else R.drawable.ic_filter_list_24px
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = "Filter",
                )
            }
        }
    }
}


