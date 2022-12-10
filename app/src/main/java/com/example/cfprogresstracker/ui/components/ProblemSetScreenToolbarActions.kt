package com.example.cfprogresstracker.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.cfprogresstracker.R

@ExperimentalAnimationApi
@Composable
fun RowScope.ProblemSetScreenActions(
    onClickFilterIcon: () -> Unit,
    isToolbarExpanded: Boolean
) {
    IconButton(onClick = onClickFilterIcon) {
        AnimatedContent(
            targetState = isToolbarExpanded,
        ) {
            val iconId = if(it) R.drawable.ic_expand_less_24px else R.drawable.ic_filter_list_24px
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = "Filter",
            )
        }
    }
}

