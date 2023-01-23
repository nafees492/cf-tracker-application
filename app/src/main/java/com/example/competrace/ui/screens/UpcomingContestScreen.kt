package com.example.competrace.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.competrace.model.Contest
import com.example.competrace.ui.components.ContestCard
import com.example.competrace.ui.components.ExpandArrow
import com.example.competrace.utils.Phase

@Composable
fun UpcomingContestScreen(
    contestLists: Map<String, List<Contest>>,
    contestListBefore: Map<String, List<Contest>>
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier.animateContentSize()
    ) {
        contestLists[Phase.CODING]?.let { list ->
            item {
                Text(
                    text = "Ongoing Contests",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
            }
            items(count = list.size) {
                ContestCard(contest = list[list.size - it - 1])
            }

            item {
                NoContestTag(list = list)
            }

        }
        contestLists[Phase.BEFORE]?.let {
            item {
                Text(
                    text = "Upcoming Contests",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
            }

            contestListBefore[Phase.WITHIN_7DAYS]?.let { list ->
                item {
                    Text(
                        text = "Next 7 Days",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                items(count = list.size) {
                    val contest = list[list.size - it - 1]
                    ContestCard(contest = contest, within7Days = true)
                }
                item {
                    NoContestTag(list = list)
                }
            }

            contestListBefore[Phase.AFTER_7DAYS]?.let { list ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(2.dp))
                            .clickable {
                                expanded = !expanded
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "After that",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        ExpandArrow(
                            expanded = expanded,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
                if (expanded) {
                    items(count = list.size) {
                        val contest = list[list.size - it - 1]
                        ContestCard(contest = contest, within7Days = false)
                    }
                    item {
                        NoContestTag(list = list)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
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

