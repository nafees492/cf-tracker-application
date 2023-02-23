package com.gourav.competrace.progress.participated_contests.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gourav.competrace.contests.model.CompetraceContest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParticipatedContestsScreen(
    participatedCodeforcesContests: List<CompetraceContest>
) {
    LazyColumn {
        items(participatedCodeforcesContests.size) {
            ParticipatedContestCard(
                contest = participatedCodeforcesContests[participatedCodeforcesContests.size - it - 1],
                modifier = Modifier.animateItemPlacement()
            )
        }

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}