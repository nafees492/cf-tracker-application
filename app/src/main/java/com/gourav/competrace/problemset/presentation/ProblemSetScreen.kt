package com.gourav.competrace.problemset.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.FilterChipScrollableRow
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.problemset.model.CodeforcesProblem

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProblemSetScreen(
    listOfCodeforcesProblem: List<CodeforcesProblem>,
    codeforcesContestListById: Map<Any, CompetraceContest>,
    tagList: ArrayList<String>,
    showTags: Boolean
) {
    var selectedChips by rememberSaveable { mutableStateOf(emptySet<String>()) }

    val isSelected: (String) -> Boolean = { selectedChips.contains(it) }

    val filteredList = if (selectedChips.isEmpty()) {
        listOfCodeforcesProblem
    } else {
        val resultList = ArrayList<CodeforcesProblem>()
        listOfCodeforcesProblem.forEach { problem ->
            problem.tags?.let { tags ->
                if (tags.containsAll(selectedChips)) resultList.add(problem)
            }
        }
        resultList
    }

    val onClickFilterChip: (String) -> Unit = {
        selectedChips = if (isSelected(it)) selectedChips.minus(it) else selectedChips.plus(it)
    }

    LazyColumn {
        if (showTags) item {
            Row(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
            ) {
                AnimatedVisibility(visible = selectedChips.isNotEmpty()) {
                    IconButton(onClick = { selectedChips = setOf() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close_24px),
                            contentDescription = "Clear all"
                        )
                    }
                }

                FilterChipScrollableRow(
                    chipList = tagList,
                    selectedChips = selectedChips,
                    onClickFilterChip = onClickFilterChip,
                )
            }
        }

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
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        items(count = filteredList.size) {
            ProblemCard(
                codeforcesProblem = filteredList[it],
                codeforcesContestListById = codeforcesContestListById,
                modifier = Modifier.animateItemPlacement(),
                selectedChips = selectedChips,
                onClickFilterChip = onClickFilterChip,
                showTags = showTags
            )
        }

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

