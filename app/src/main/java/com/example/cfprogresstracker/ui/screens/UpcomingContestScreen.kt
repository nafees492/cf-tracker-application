package com.example.cfprogresstracker.ui.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.ui.components.ContestInfoCard
import com.example.cfprogresstracker.ui.components.RotatingArrow
import com.example.cfprogresstracker.utils.Phase
import com.example.cfprogresstracker.utils.addEventToCalendar

@Composable
fun UpcomingContestScreen(
    contestLists: Map<String, List<Contest>>,
    contestListBefore: Map<String, List<Contest>>
) {

    val context = LocalContext.current

    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier.animateContentSize()
    ){
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
                    val contest = list[list.size - it - 1]
                    val onClickAddToCalender : () -> Unit = {
                        addEventToCalendar(
                            context = context,
                            title = contest.name,
                            startTime = contest.startTimeInMillis(),
                            endTime = contest.endTimeInMillis(),
                            location = contest.getContestLink(),
                            description = ""
                        )
                    }
                    ContestInfoCard(contest = contest, within3days = true, onClickAddToCalender = onClickAddToCalender)
                }
                item {
                    NoContestTag(list = list)
                }
            }

            contestListBefore[Phase.MORE]?.let { list ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                expanded = !expanded
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "After that",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        RotatingArrow(
                            expanded = expanded,
                            onClick = { expanded = !expanded },
                            modifier = Modifier.padding(vertical = 4.dp, horizontal =  16.dp)
                        )
                    }

                }
                if(expanded) {
                    items(count = list.size) {
                        val contest = list[list.size - it - 1]
                        val onClickAddToCalender: () -> Unit = {
                            addEventToCalendar(
                                context = context,
                                title = contest.name,
                                startTime = contest.startTimeInMillis(),
                                endTime = contest.endTimeInMillis(),
                                location = contest.getContestLink(),
                                description = ""
                            )
                        }
                        ContestInfoCard(
                            contest = contest,
                            within3days = false,
                            onClickAddToCalender = onClickAddToCalender
                        )
                    }
                    item {
                        NoContestTag(list = list)
                    }
                }
            }
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

