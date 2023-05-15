package com.gourav.competrace.progress.user_submissions.presentation

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
import com.gourav.competrace.app_core.util.UiText
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.problemset.model.CodeforcesProblem
import com.gourav.competrace.progress.user_submissions.model.Submission

@OptIn(
    ExperimentalFoundationApi::class,
)
@Composable
fun UserSubmissionsScreen(
    submittedProblemsWithSubmissions: List<Pair<CodeforcesProblem, ArrayList<Submission>>>,
    codeforcesContestListById: Map<Any, CompetraceContest>,
    showTags: Boolean,
    currentSelection: UiText,
    updateCurrentSelection: (UiText) -> Unit
) {
    var selectedChips by rememberSaveable { mutableStateOf(emptySet<String>()) }

    val isSelected: (String) -> Boolean = { selectedChips.contains(it) }

    val filteredList = if (selectedChips.isEmpty()) {
        submittedProblemsWithSubmissions
    } else {
        submittedProblemsWithSubmissions.filter {
            it.first.tags?.containsAll(selectedChips) ?: false
        }
    }

    val problemCountOnScreen by remember(filteredList) {
        mutableStateOf(filteredList.size)
    }

    val onClickFilterChip: (String) -> Unit = {
        selectedChips = if (isSelected(it)) selectedChips.minus(it) else selectedChips.plus(it)
    }

    var selectedCodeforcesProblem by remember {
        mutableStateOf<Pair<CodeforcesProblem, ArrayList<Submission>>?>(null)
    }

    var isSheetVisible by remember {
        mutableStateOf(false)
    }

    fun onClickSubmissionCard(it: Pair<CodeforcesProblem, ArrayList<Submission>>?) {
        selectedCodeforcesProblem = it
        isSheetVisible = true
    }

    val scrollConnectionState = rememberScrollConnectionState()

    Scaffold(
        topBar = {
            Surface(modifier = Modifier.addScrollConnection(scrollConnectionState)) {
                UserSubmissionFilterRow(
                    currentSelection = currentSelection,
                    problemCountOnScreen = problemCountOnScreen,
                    updateCurrentSelection = updateCurrentSelection
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollConnectionState.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
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
                            text = stringResource(id = R.string.no_problem_found),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            items(filteredList.size) {
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

    UserSubmissionsModelBottomSheet(
        isVisible = isSheetVisible,
        onDismiss = { isSheetVisible = false },
        codeforcesProblem = selectedCodeforcesProblem?.first,
        submissions = selectedCodeforcesProblem?.second,
        codeforcesContest =
        codeforcesContestListById[selectedCodeforcesProblem?.first?.contestId ?: 0]
    )
}