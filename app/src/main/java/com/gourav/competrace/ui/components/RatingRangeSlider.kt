package com.gourav.competrace.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingRangeSlider(
    start: Int,
    end: Int,
    updateStartAndEnd: (Int, Int) -> Unit
) {

    var sliderPosition by remember {
        mutableStateOf(start*1f..end*1f)
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Rating Range: ${sliderPosition.start.roundToInt()} - ${sliderPosition.endInclusive.roundToInt()}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
        RangeSlider(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .semantics { contentDescription = "Localized Description" },
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
            },
            valueRange = 800f..3500f,
            onValueChangeFinished = {
                // launch some business logic update with the state you hold
                // viewModel.updateSelectedSliderValue(sliderPosition)
                updateStartAndEnd(sliderPosition.start.roundToInt(), sliderPosition.endInclusive.roundToInt())
            },
            steps = 26,
        )
    }
}
