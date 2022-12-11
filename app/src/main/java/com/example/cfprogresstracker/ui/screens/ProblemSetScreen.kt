package com.example.cfprogresstracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
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
fun ProblemSetScreen(
    listOfProblem: List<Problem>,
    contestListById: MutableMap<Int, Contest>,
    tagList: ArrayList<String>
) {
    var selectedChips by rememberSaveable { mutableStateOf(emptySet<String>()) }

    val isSelected: (String) -> Boolean = { selectedChips.contains(it) }

    val filteredList = if (selectedChips.isEmpty()) {
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

    val onClickFilterChip: (String) -> Unit = {
        selectedChips = if (isSelected(it)) selectedChips.minus(it) else selectedChips.plus(it)
    }

    val trailingIcon: @Composable ((visible: Boolean) -> Unit) = {
        AnimatedVisibility(visible = it) {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = ""
            )
        }
    }

    LazyColumn {
        item {
            LazyRow(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 4.dp)
            ) {
                items(tagList.size) {
                    FilterChip(
                        selected = isSelected(tagList[it]),
                        onClick = { onClickFilterChip(tagList[it]) },
                        label = { Text(text = tagList[it]) },
                        trailingIcon = { trailingIcon(isSelected(tagList[it])) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
            Divider()
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

