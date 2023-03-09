package com.gourav.competrace.contests.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.ExpandArrow
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.app_core.util.Phase

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UpcomingContestScreen(
    contests: List<CompetraceContest>,
    selectedIndex: Int
) {
    val onGoingContest = remember(contests) {
        contests.filter { it.phase == Phase.CODING }
    }
    val within7Days = remember(contests) {
        contests.filter { it.phase == Phase.BEFORE && it.within7Days }
    }
    val after7Days = remember(contests) {
        contests.filter { it.phase == Phase.BEFORE && !it.within7Days }
    }

    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    fun generateKey(id: Int) = "s${selectedIndex}-i{$id}"

    LazyColumn(
        modifier = Modifier.animateContentSize()
    ) {
        item(key = generateKey(1)) {
            Text(
                text = stringResource(id = R.string.ongoing_contests),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                modifier = Modifier
                    .padding(8.dp)
                    .animateItemPlacement(
                        animationSpec = tween()
                    )

            )
        }

        onGoingContest.let { list ->
            items(count = list.size, key = { list[it].hashCode() }) {
                ContestCard(
                    contest = list[it],
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween()
                    )
                )
            }

            item(key = generateKey(2)) {
                NoContestTag(
                    isDisplayed = list.isEmpty(),
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween()
                    )
                )
            }

        }

        item(key = generateKey(3)) {
            Text(
                text = stringResource(id = R.string.upcoming_contests),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                modifier = Modifier
                    .padding(8.dp)
                    .animateItemPlacement(
                        animationSpec = tween()
                    )
            )
        }

        within7Days.let { list ->
            item(key = generateKey(4)) {
                Text(
                    text = stringResource(id = R.string.next_7_days),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .animateItemPlacement(
                            animationSpec = tween()
                        )
                )
            }

            items(count = list.size, key = { list[it].hashCode() }) {
                if (list[it].within7Days) {
                    ContestCard(
                        contest = list[it],
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = tween()
                        )
                    )
                }
            }

            item(key = generateKey(5)) {
                NoContestTag(
                    isDisplayed = list.isEmpty(),
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween()
                    )
                )
            }
        }

        after7Days.let { list ->
            item(key = generateKey(6)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            expanded = !expanded
                        }
                        .animateItemPlacement(
                            animationSpec = tween()
                        )
                ) {
                    Text(
                        text = stringResource(id = R.string.after_7_days),
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
                items(count = list.size, key = { list[it].hashCode() }) {
                    if (!list[it].within7Days) {
                        ContestCard(
                            contest = list[it],
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween()
                            )
                        )
                    }
                }

                item(key = generateKey(7)) {
                    NoContestTag(
                        isDisplayed = list.isEmpty(),
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = tween()
                        )
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun NoContestTag(isDisplayed: Boolean, modifier: Modifier = Modifier) {
    if (isDisplayed) {
        Text(
            text = stringResource(id = R.string.no_contest),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

