package com.example.cfprogresstracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipRow(
    chipList: List<String>,
    selectedChips: Set<String>,
    onClickFilterChip: (String) -> Unit
) {
    val isSelected: (String) -> Boolean = { selectedChips.contains(it) }

    val trailingIcon: @Composable ((visible: Boolean) -> Unit) = {
        AnimatedVisibility(visible = it) {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = ""
            )
        }
    }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        crossAxisSpacing = 8.dp,
        mainAxisSpacing = 8.dp
    ) {
        chipList.forEach {
            FilterChip(
                selected = isSelected(it),
                onClick = { onClickFilterChip(it) },
                label = { Text(text = it) },
                trailingIcon = { trailingIcon(isSelected(it)) }
            )
        }
    }
}