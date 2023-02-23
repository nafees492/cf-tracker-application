package com.gourav.competrace.contests.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gourav.competrace.app_core.ui.components.ExpandArrow
import com.gourav.competrace.contests.model.CompetraceContest

@Composable
fun UpcomingContestScreen(
    onGoingContest: List<CompetraceContest>?,
    upComingContests: List<CompetraceContest>?
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    var hasNoItemInNext7Days by remember{
        mutableStateOf(true)
    }

    var hasNoItemAfterNext7Days  by remember{
        mutableStateOf(true)
    }

    LazyColumn(
        modifier = Modifier.animateContentSize()
    ) {
        item {
            Text(
                text = "Ongoing Contests",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        }

        onGoingContest?.let { list ->
            items(count = list.size) {
                ContestCard(contest = list[it])
            }

            item {
                NoContestTag(isDisplayed = list.isEmpty())
            }

        }

        item {
            Text(
                text = "Upcoming Contests",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        }

        upComingContests?.let { list ->
            item {
                Text(
                    text = "Next 7 Days",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            items(count = list.size) {
                val contest = list[it]
                if(contest.within7Days) {
                    ContestCard(contest = contest)
                    hasNoItemInNext7Days = false
                }
            }

            item {
                NoContestTag(isDisplayed = hasNoItemInNext7Days)
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            expanded = !expanded
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "After 7 Days",
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
                    val contest = list[it]
                    if (!contest.within7Days){
                        ContestCard(contest = contest)
                        hasNoItemAfterNext7Days = false
                    }
                }

                item {
                    NoContestTag(isDisplayed = hasNoItemAfterNext7Days)
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun NoContestTag(isDisplayed: Boolean) {
    if (isDisplayed) {
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

