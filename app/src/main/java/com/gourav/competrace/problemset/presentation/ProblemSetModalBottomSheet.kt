package com.gourav.competrace.problemset.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceModelBottomSheet
import com.gourav.competrace.app_core.ui.components.FilterChipFlowRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingModelBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    ratingRange: IntRange,
    updateRatingRange: (Int, Int) -> Unit,
) {
    CompetraceModelBottomSheet(isVisible = isVisible, onDismiss = onDismiss) {
        RatingRangeSlider(
            start = ratingRange.first, end = ratingRange.last,
            updateStartAndEnd = updateRatingRange,
            modifier = Modifier.padding(top = 16.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { updateRatingRange(800, 3500) },
                Modifier.padding(horizontal = 8.dp),
                enabled = ratingRange != 800..3500
            ) {
                Text(text = stringResource(id = R.string.reset))
            }
        }
        Spacer(modifier = Modifier.height(54.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagModelBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    allTags: List<String>,
    selectedTags: Set<String>,
    updateSelectedTags: (String) -> Unit,
    clearSelectedTags: () -> Unit
 ) {
    val sheetState = rememberModalBottomSheetState(true)

    CompetraceModelBottomSheet(
        isVisible = isVisible,
        onDismiss = onDismiss,
        sheetState = sheetState
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize()
        ) {
            item {
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                FilterChipFlowRow(
                    chipList = allTags,
                    selectedChips = selectedTags,
                    onClickFilterChip = updateSelectedTags,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = clearSelectedTags,
                        Modifier.padding(horizontal = 8.dp),
                        enabled = selectedTags.isNotEmpty()
                    ) {
                        Text(text = stringResource(R.string.reset))
                    }
                }
            }
        }
    }
}

