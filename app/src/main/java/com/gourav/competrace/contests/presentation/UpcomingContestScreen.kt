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
import com.gourav.competrace.contests.model.ContestScreenState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UpcomingContestScreen(
    state: ContestScreenState,
    onClickNotificationIcon: (CompetraceContest) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    fun generateKey(id: Int) = "s${state.selectedIndex}-i{$id}"

    LazyColumn(
        modifier = modifier.animateContentSize()
    ) {
        if (state.ongoingContests.isNotEmpty()) {
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

            items(count = state.ongoingContests.size, key = { state.ongoingContests[it].hashCode() }) {
                ContestCard(
                    contest = state.ongoingContests[it],
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween()
                    ),
                    onClickNotificationIcon = onClickNotificationIcon,
                    notificationContestIdList = state.notificationContestIds
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

        state.next7DaysContests.let { list ->
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
                ContestCard(
                    contest = list[it],
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween()
                    ),
                    onClickNotificationIcon = onClickNotificationIcon,
                    notificationContestIdList = state.notificationContestIds
                )
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

        state.after7DaysContests.let { list ->
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
                    ContestCard(
                        contest = list[it],
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = tween()
                        ),
                        onClickNotificationIcon = onClickNotificationIcon,
                        notificationContestIdList = state.notificationContestIds
                    )
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

            item {
                Spacer(modifier = Modifier.height(128.dp))
            }
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

