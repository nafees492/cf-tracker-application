package com.gourav.competrace.problemset.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.FilterChipScrollableRow
import com.gourav.competrace.problemset.model.ProblemSetScreenState

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProblemSetScreen(
    state: ProblemSetScreenState,
    updateSelectedChips: (String) -> Unit,
    clearSelectedChips: () -> Unit,
) {
    LazyColumn {
        if (state.isTagsVisible) item {
            Row(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
            ) {
                AnimatedVisibility(visible = state.selectedTags.isNotEmpty()) {
                    IconButton(onClick = clearSelectedChips) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close_24px),
                            contentDescription = "Clear all"
                        )
                    }
                }

                FilterChipScrollableRow(
                    chipList = state.allTags,
                    selectedChips = state.selectedTags,
                    onClickFilterChip = updateSelectedChips,
                )
            }
        }

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

