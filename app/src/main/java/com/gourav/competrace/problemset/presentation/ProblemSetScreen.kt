package com.gourav.competrace.problemset.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    updateSelectedTags: (String) -> Unit,
    clearSelectedTags: () -> Unit,
    updateRatingRange: (Int, Int) -> Unit
) {
    val scrollConnectionState = rememberScrollConnectionState()

    var isRatingSheetVisible by remember {
        mutableStateOf(false)
    }

    var isTagSheetVisible by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            Surface(modifier = Modifier.addScrollConnection(scrollConnectionState)) {
                ProblemSetFilterRow(
                    showRatingSheet = { isRatingSheetVisible = true },
                    showTagSheet = { isTagSheetVisible = true },
                    ratingRange = state.ratingRangeValue,
                    selectedTags = state.selectedTags,
                    isTagsVisible = state.isTagsVisible
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
                    onClickFilterChip = updateSelectedTags,
                    modifier = Modifier.animateItemPlacement()
                )
            }

            item {
                Spacer(modifier = Modifier.height(128.dp))
            }
        }
    }

    RatingModelBottomSheet(
        isVisible = isRatingSheetVisible,
        onDismiss = { isRatingSheetVisible = false },
        ratingRange = state.ratingRangeValue,
        updateRatingRange = updateRatingRange,
    )

    TagModelBottomSheet(
        isVisible = isTagSheetVisible,
        onDismiss = { isTagSheetVisible = false },
        allTags = state.allTags,
        selectedTags = state.selectedTags,
        updateSelectedTags = updateSelectedTags,
        clearSelectedTags = clearSelectedTags
    )
}

