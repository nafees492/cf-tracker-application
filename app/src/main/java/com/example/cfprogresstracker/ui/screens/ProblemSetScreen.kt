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
import com.example.cfprogresstracker.ui.components.ProblemCard

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProblemSetScreen(listOfProblem: List<Problem>, contestListById: MutableMap<Int, Contest>) {
    var selectedChips by rememberSaveable { mutableStateOf(emptySet<String>()) }

    val isSelected: (String) -> Boolean = { selectedChips.contains(it) }

    var filteredList = if (selectedChips.isEmpty()) {
        listOfProblem
    } else {
        val resultList = ArrayList<Problem>()
        listOfProblem.forEach { problem ->
            problem.tags?.let { tags ->
                if (tags.containsAll(selectedChips)) resultList.add(problem)
            }
        }
        resultList
    }

    var onClickFilterChip: (String) -> Unit = {
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

        items(count = filteredList.size) {
            ProblemCard(
                problem = filteredList[it],
                contestListById = contestListById,
                modifier = Modifier.animateItemPlacement(),
                selectedChips = selectedChips,
                onClickFilterChip = onClickFilterChip
            )
        }

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

