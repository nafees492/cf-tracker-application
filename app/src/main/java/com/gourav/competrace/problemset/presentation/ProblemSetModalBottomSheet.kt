package com.gourav.competrace.problemset.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gourav.competrace.app_core.ui.components.CompetraceModelBottomSheet
import com.gourav.competrace.app_core.ui.components.FilterChipFlowRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingAndTagModelBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    ratingRange: IntRange,
    allTags: List<String>,
    selectedTags: Set<String>,
    updateRatingRange: (Int, Int) -> Unit,
    updateSelectedChips: (String) -> Unit,
){
    CompetraceModelBottomSheet(isVisible = isVisible, onDismiss = onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            RatingRangeSlider(
                start = ratingRange.first, end = ratingRange.last,
                updateStartAndEnd = updateRatingRange
            )
            Text(
                text = "Tags",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            FilterChipFlowRow(
                chipList = allTags,
                selectedChips = selectedTags,
                onClickFilterChip = updateSelectedChips,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
