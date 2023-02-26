package com.gourav.competrace.app_core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FilterChipScrollableRow(
    chipList: List<String>,
    selectedChips: Set<String>,
    onClickFilterChip: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelStyle: TextStyle = MaterialTheme.typography.labelSmall
) {
    fun isSelected(it: String) = selectedChips.contains(it)

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(4.dp)
    ) {
        items(chipList.size) {
            ElevatedFilterChip(
                selected = isSelected(chipList[it]),
                onClick = { onClickFilterChip(chipList[it]) },
                label = {
                    Text(
                        text = chipList[it],
                        style = labelStyle
                    )
                },
                leadingIcon = {
                    AnimatedVisibility(visible = isSelected(chipList[it])) {
                        Icon(
                            imageVector = Icons.Rounded.Done,
                            contentDescription = ""
                        )
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .animateItemPlacement(),
            )
        }
    }
}