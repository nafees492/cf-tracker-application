package com.example.cfprogresstracker.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.ui.components.ContestInfoCard
import com.example.cfprogresstracker.ui.components.NormalButton
import com.example.cfprogresstracker.viewmodel.Phase

@Composable
fun ContestsScreen(
    contestLists: Map<String, List<Contest>>,
    contestListBefore: Map<String, List<Contest>>,
    toolBarScrollBehavior: TopAppBarScrollBehavior,
    onClickFinishedContests: () -> Unit
) {

    LazyColumn(
        modifier = Modifier.nestedScroll(toolBarScrollBehavior.nestedScrollConnection)
    ) {
        contestLists[Phase.CODING]?.let { list ->
            item {
                Text(
                    text = "Current Contests",
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
            }
            items(count = list.size) {
                ContestInfoCard(contest = list[list.size - it - 1])
            }

            item {
                NoContestTag(list = list)
            }

        }
        contestLists[Phase.BEFORE]?.run {
            item {
                Text(
                    text = "Upcoming Contests",
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
            }

            contestListBefore[Phase.WITHIN_2DAYS]?.let { list ->
                item {
                    Text(
                        text = "Next 3 Days",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                items(count = list.size) {
                    ContestInfoCard(contest = list[list.size - it - 1], true)
                }
                item {
                    NoContestTag(list = list)
                }
            }

            contestListBefore[Phase.MORE]?.let { list ->
                item {
                    Text(
                        text = "After that",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                items(count = list.size) {
                    ContestInfoCard(contest = list[list.size - it - 1], false)
                }
                item {
                    NoContestTag(list = list)
                }
            }
        }
        item {
            Divider(modifier = Modifier.padding(8.dp))
            NormalButton(
                text = "Finished Contests",
                onClick = onClickFinishedContests,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun NoContestTag(list: List<Contest>) {
    if (list.isEmpty()) {
        Text(
            text = "No Contest",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

