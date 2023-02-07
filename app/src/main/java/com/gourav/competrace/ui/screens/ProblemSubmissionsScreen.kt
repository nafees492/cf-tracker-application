package com.gourav.competrace.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gourav.competrace.model.Contest
import com.gourav.competrace.model.Problem
import com.gourav.competrace.model.Submission
import com.gourav.competrace.ui.components.ProblemSubmissionCard
import com.gourav.competrace.ui.components.ProblemSubmissionScreenBottomSheetContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun ProblemSubmissionScreen(
    submittedProblemsWithSubmissions: ArrayList<Pair<Problem, ArrayList<Submission>>>,
    contestListById: MutableMap<Int, Contest>,
    showTags: Boolean
) {

    var selectedChips by rememberSaveable { mutableStateOf(emptySet<String>()) }

    val isSelected: (String) -> Boolean = { selectedChips.contains(it) }

    val filteredList = if (selectedChips.isEmpty()) {
        submittedProblemsWithSubmissions
    } else {
        val resultList = ArrayList<Pair<Problem, ArrayList<Submission>>>()
        submittedProblemsWithSubmissions.forEach { problemWithSubmission ->
            problemWithSubmission.first.tags?.let { tags ->
                if (tags.containsAll(selectedChips)) resultList.add(problemWithSubmission)
            }
        }
        resultList
    }

    val onClickFilterChip: (String) -> Unit = {
        selectedChips = if (isSelected(it)) selectedChips.minus(it) else selectedChips.plus(it)
    }

    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true,
    )

    var selectedProblem by remember {
        mutableStateOf<Pair<Problem, ArrayList<Submission>>?>(null)
    }

    val onClickSubmissionCard: (Pair<Problem, ArrayList<Submission>>?) -> Unit = {
        selectedProblem = it
        coroutineScope.launch {
            delay(100)
            modalSheetState.show()
        }
    }

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
        sheetContent = {
            ProblemSubmissionScreenBottomSheetContent(
                problem = selectedProblem?.first,
                submissions = selectedProblem?.second,
                contest = contestListById[selectedProblem?.first?.contestId]
            )
        }
    ) {
        LazyColumn {
            item {
                if (filteredList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth()
                            .animateItemPlacement(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Problem Found!",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(8.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                if (selectedChips.isNotEmpty()) {
                    ElevatedAssistChip(
                        onClick = { selectedChips = emptySet() },
                        label = { Text("Clear All", style = MaterialTheme.typography.labelMedium) },
                        enabled = true,
                        modifier = Modifier
                            .padding(8.dp)
                            .animateItemPlacement()
                    )
                }
            }

            items(filteredList.size) {
                ProblemSubmissionCard(
                    problem = filteredList[it].first,
                    submissions = filteredList[it].second,
                    contestListById = contestListById,
                    showTags = showTags,
                    selectedChips = selectedChips,
                    onClickFilterChip = onClickFilterChip,
                    onClick = { onClickSubmissionCard(filteredList[it]) },
                    modifier = Modifier.animateItemPlacement(),
                )
            }
        }
    }
}