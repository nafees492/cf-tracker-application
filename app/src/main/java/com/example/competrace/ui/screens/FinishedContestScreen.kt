package com.example.competrace.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.competrace.model.Contest
import com.example.competrace.ui.components.ContestCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinishedContestScreen(
    finishedContests: List<Contest>
) {
    LazyColumn {
        items(finishedContests.size) {
            ContestCard(contest = finishedContests[it], modifier = Modifier.animateItemPlacement())
        }

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}