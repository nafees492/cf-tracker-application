package com.example.cfprogresstracker.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.model.Problem
import com.example.cfprogresstracker.model.Submission
import com.example.cfprogresstracker.ui.components.SubmissionCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UserSubmissionScreen(
    submittedProblemsWithSubmissions: ArrayList<Pair<Problem, ArrayList<Submission>>>,
    contestListById: MutableMap<Int, Contest>
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

    LazyColumn {
        item {
            if (filteredList.isEmpty()) {
                Column(
                    modifier = Modifier.height(120.dp).fillMaxWidth().animateItemPlacement(),
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
            if(selectedChips.isNotEmpty()){
                AssistChip(
                    onClick = { selectedChips = emptySet() },
                    label = { Text("Clear All") },
                    enabled = true,
                    modifier = Modifier.padding(8.dp).animateItemPlacement()
                )
            }
        }

        items(filteredList.size) {
            SubmissionCard(
                problem = filteredList[it].first,
                submissions = filteredList[it].second,
                contestListById = contestListById,
                modifier = Modifier.animateItemPlacement(),
                selectedChips = selectedChips,
                onClickFilterChip = onClickFilterChip
            )
        }
    }
}