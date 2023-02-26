package com.gourav.competrace.app_core.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetracePlatformRow(
    selectedTabIndex: Int,
    tabTitles: List<String>,
    onClickTab: (Int) -> Unit,
    scrollState: LazyListState = rememberLazyListState()
) {

    LaunchedEffect(key1 = selectedTabIndex){
        scrollState.animateScrollToItem(selectedTabIndex, -140)
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 4.dp),
        state = scrollState,
    ) {
        items(tabTitles.size) {
            FilterChip(
                selected = selectedTabIndex == it, onClick = { (onClickTab(it)) },
                label = {
                    Text(
                        text = tabTitles[it],
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}