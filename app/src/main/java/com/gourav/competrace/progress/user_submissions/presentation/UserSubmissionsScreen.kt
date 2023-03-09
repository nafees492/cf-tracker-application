package com.gourav.competrace.progress.user_submissions.presentation

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.problemset.model.CodeforcesProblem
import com.gourav.competrace.progress.user_submissions.model.Submission
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun UserSubmissionsScreen(
    submittedProblemsWithSubmissions: List<Pair<CodeforcesProblem, ArrayList<Submission>>>,
    codeforcesContestListById: Map<Any, CompetraceContest>,
    showTags: Boolean
) {
    val scope = rememberCoroutineScope()

    var selectedChips by rememberSaveable { mutableStateOf(emptySet<String>()) }

    val isSelected: (String) -> Boolean = { selectedChips.contains(it) }

    val filteredList = if (selectedChips.isEmpty()) {
        submittedProblemsWithSubmissions
    } else {
        submittedProblemsWithSubmissions.filter {
            it.first.tags?.containsAll(selectedChips) ?: false
        }
    }

    val onClickFilterChip: (String) -> Unit = {
        selectedChips = if (isSelected(it)) selectedChips.minus(it) else selectedChips.plus(it)
    }

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true,
    )

    var selectedCodeforcesProblem by remember {
        mutableStateOf<Pair<CodeforcesProblem, ArrayList<Submission>>?>(null)
    }

    fun onClickSubmissionCard(it: Pair<CodeforcesProblem, ArrayList<Submission>>?) {
        selectedCodeforcesProblem = it
        scope.launch {
            delay(100)
            modalSheetState.show()
        }
    }

    BackHandler(modalSheetState.isVisible) {
        scope.launch { modalSheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
        sheetContent = {
            UserSubmissionsScreenBottomSheetContent(
                codeforcesProblem = selectedCodeforcesProblem?.first,
                submissions = selectedCodeforcesProblem?.second,
                codeforcesContest = codeforcesContestListById[selectedCodeforcesProblem?.first?.contestId
                    ?: 0]
            )
        }
    ) {
        LazyColumn {
            item(key = "no-problem-tag") {
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

            item(key = "clear-all-chip") {
                if (selectedChips.isNotEmpty()) {
                    ElevatedAssistChip(
                        onClick = { selectedChips = emptySet() },
                        label = {
                            Text(
                                stringResource(R.string.clear_all),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        enabled = true,
                        modifier = Modifier
                            .padding(8.dp)
                            .animateItemPlacement()
                    )
                }
            }

            items(filteredList.size, key = { filteredList[it].hashCode() }) {
                ProblemSubmissionCard(
                    codeforcesProblem = filteredList[it].first,
                    submissions = filteredList[it].second,
                    codeforcesContestListById = codeforcesContestListById,
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