package com.gourav.competrace.app_core.ui.components


import androidx.compose.animation.*
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpandArrow(expanded: Boolean, modifier: Modifier = Modifier) {
    AnimatedContent(
        targetState = expanded,
        transitionSpec = {
            if (targetState) {
                slideInVertically { height -> height } + fadeIn() with
                        slideOutVertically { height -> -height } + fadeOut()
            } else {
                slideInVertically { height -> -height } + fadeIn() with
                        slideOutVertically { height -> height } + fadeOut()
            }.using(
                SizeTransform(clip = false)
            )
        }
    ) {
        val iconId = if (it) R.drawable.ic_expand_less_24px else R.drawable.ic_expand_more_24px
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = "Expandable Arrow",
            modifier = modifier.size(24.dp)
        )
    }
}