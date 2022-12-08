package com.example.cfprogresstracker.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.ui.components.ContestInfoCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinishedContestScreen(
    finishedContests: List<Contest>
) {
    LazyColumn {
        item {
            if (finishedContests.isEmpty()) {
                Column(
                    modifier = Modifier.height(120.dp).fillMaxWidth().animateItemPlacement(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Contests Found!",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        items(finishedContests.size) {
            ContestInfoCard(contest = finishedContests[it], modifier = Modifier.animateItemPlacement())
        }

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}