package com.gourav.competrace.problemset.presentation

import android.text.style.BulletSpan
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
                Text(text = "Reset")
            }
        }
        Spacer(modifier = Modifier.height(54.dp))
    }
}

@Composable
fun TagModelBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    allTags: List<String>,
    selectedTags: Set<String>,
    updateSelectedChips: (String) -> Unit,
) {
    CompetraceModelBottomSheet(isVisible = isVisible, onDismiss = onDismiss) {
        Text(
            text = "Tags",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilterChipFlowRow(
            chipList = allTags,
            selectedChips = selectedTags,
            onClickFilterChip = updateSelectedChips,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

