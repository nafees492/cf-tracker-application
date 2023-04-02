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
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.problemset.model.CompetraceProblem

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProblemSetScreen(
    problems: List<CompetraceProblem>,
    codeforcesContestListById: Map<Any, CompetraceContest>,
    allTags: List<String>,
    selectedChips: Set<String>,
    updateSelectedChips: (String) -> Unit,
    clearSelectedChips: () -> Unit,
    showTags: Boolean
) {
    LazyColumn {

        if (showTags) item {
            Row(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
            ) {
                AnimatedVisibility(visible = selectedChips.isNotEmpty()) {
                    IconButton(onClick = clearSelectedChips) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close_24px),
                            contentDescription = "Clear all"
                        )
                    }
                }

                FilterChipScrollableRow(
                    chipList = allTags,
                    selectedChips = selectedChips,
                    onClickFilterChip = updateSelectedChips,
                )
            }
        }

        item {
            if (problems.isEmpty()) {
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

        items(count = problems.size) {
            ProblemCard(
                problem = problems[it],
                contestName = codeforcesContestListById[problems[it].contestId ?: 0]?.name,
                showTags = showTags,
                selectedChips = selectedChips,
                onClickFilterChip = updateSelectedChips,
                modifier = Modifier.animateItemPlacement()
            )
        }

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

