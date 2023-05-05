package com.gourav.competrace.problemset.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.addScrollConnection
import com.gourav.competrace.app_core.ui.components.rememberScrollConnectionState
import com.gourav.competrace.problemset.model.ProblemSetScreenState

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProblemSetScreen(
    state: ProblemSetScreenState,
    updateSelectedChips: (String) -> Unit,
    clearSelectedChips: () -> Unit,
    updateRatingRange: (Int, Int) -> Unit
) {
    val scrollConnectionState = rememberScrollConnectionState()

    var isSheetVisible by rememberSaveable {
        mutableStateOf(false)
    }

    val tagString = buildString {
        if (state.selectedTags.isEmpty()) append("None")
        state.selectedTags.forEach { tag ->
            append(tag)
            if (tag != state.selectedTags.last()) append(", ")
        }
    }

    Scaffold(
        topBar = {
            Surface(modifier = Modifier.addScrollConnection(scrollConnectionState)) {
                ProblemSetFilterRow(
                    showBottomSheet = { isSheetVisible = true },
                    ratingRange = state.ratingRangeValue,
                    tagString = tagString
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .nestedScroll(scrollConnectionState.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                if (state.problems.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth()
                            .animateItemPlacement(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_problem_found),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            items(count = state.problems.size) {
                ProblemCard(
                    problem = state.problems[it],
                    contestName = state.getContestName(state.problems[it]),
                    isTagsVisible = state.isTagsVisible,
                    selectedTags = state.selectedTags,
                    onClickFilterChip = updateSelectedChips,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }

    RatingAndTagModelBottomSheet(
        isVisible = isSheetVisible,
        onDismiss = { isSheetVisible = false },
        ratingRange = state.ratingRangeValue,
        allTags = state.allTags,
        selectedTags = state.selectedTags,
        updateRatingRange = updateRatingRange,
        updateSelectedChips = updateSelectedChips
    )
}

