package com.example.cfprogresstracker.ui.components


import androidx.compose.animation.*
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cfprogresstracker.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RotatingArrow (
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    IconButton(
        onClick = onClick,
        modifier = modifier.size(24.dp),
        content = {
            AnimatedContent(
                targetState = expanded,
                transitionSpec = {
                    // Compare the incoming number with the previous number.
                    if (targetState) {
                        // If the target number is larger, it slides up and fades in
                        // while the initial (smaller) number slides up and fades out.
                        slideInVertically { height -> height } + fadeIn() with
                                slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        // If the target number is smaller, it slides down and fades in
                        // while the initial number slides down and fades out.
                        slideInVertically { height -> -height } + fadeIn() with
                                slideOutVertically { height -> height } + fadeOut()
                    }.using(
                        // Disable clipping since the faded slide-in/out should
                        // be displayed out of bounds.
                        SizeTransform(clip = false)
                    )
                }
            ) {
                val iconId = if(it) R.drawable.ic_expand_less_24px else R.drawable.ic_expand_more_24px
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = "Expandable Arrow",
                )
            }
        }
    )

}