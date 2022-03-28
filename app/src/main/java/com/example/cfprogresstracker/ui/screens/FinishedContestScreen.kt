package com.example.cfprogresstracker.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.ui.components.ContestInfoCard

@Composable
fun FinishedContestScreen(
    finishedContests: List<Contest>
) {
    LazyColumn {
        items(finishedContests.size) {
            ContestInfoCard(contest = finishedContests[it])
        }
    }
}