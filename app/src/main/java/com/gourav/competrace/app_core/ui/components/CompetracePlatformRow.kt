package com.gourav.competrace.app_core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gourav.competrace.app_core.util.Sites

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetracePlatformRow(
    selectedTabIndex: Int,
    platforms: List<Sites>,
    onClickTab: (Int) -> Unit,
    scrollState: LazyListState = rememberLazyListState()
) {

    val haptic = LocalHapticFeedback.current

    LaunchedEffect(key1 = selectedTabIndex) {
        scrollState.animateScrollToItem(selectedTabIndex, -140)
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 4.dp),
        state = scrollState,
    ) {
        items(platforms.size) {
            FilterChip(
                selected = selectedTabIndex == it,
                onClick = {
                    onClickTab(it)
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                label = {
                    Text(
                        text = platforms[it].title,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = platforms[it].iconId),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clip(CircleShape)
                            .size(24.dp)
                    )
                },
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}