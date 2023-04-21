package com.gourav.competrace.progress.user.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceButton
import com.gourav.competrace.app_core.ui.components.MyCircularProgressIndicator
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.progress.user.model.User
import com.gourav.competrace.progress.user_submissions.presentation.UserSubmissionsViewModel
import com.gourav.competrace.app_core.ui.FailureScreen
import com.gourav.competrace.app_core.util.ColorUtils
import com.skydoves.landscapist.animation.crossfade.CrossfadePlugin
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@Composable
fun ProgressScreen(
    user: User,
    goToSubmission: () -> Unit,
    goToParticipatedContests: () -> Unit,
    userSubmissionsViewModel: UserSubmissionsViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlideImage(
                    // CoilImage, FrescoImage
                    imageModel = { user.titlePhoto },
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    component = rememberImageComponent {
                        +ShimmerPlugin(
                            baseColor = MaterialTheme.colorScheme.surface,
                            highlightColor = MaterialTheme.colorScheme.onSurface
                        )
                        +PlaceholderPlugin.Failure(
                            painterResource(
                                id = R.drawable.broken_image_48px
                            )
                        )
                    },
                )
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    var fullName = ""
                    user.firstName?.let { fullName = fullName.plus("$it ") }
                    user.lastName?.let { fullName = fullName.plus(it) }
                    if (fullName.isBlank()) fullName = user.handle
                    Text(
                        text = fullName,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (fullName != user.handle) Text(
                        text = user.handle,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Rating: ${user.rating}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = ColorUtils.getRatingTextColor(rating = user.rating)
                    )

                    Text(
                        text = "Max Rating: ${user.maxRating}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = ColorUtils.getRatingTextColor(rating = user.maxRating)
                    )

                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompetraceButton(
                    text = stringResource(id = R.string.participated_contests),
                    onClick = goToParticipatedContests,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
                CompetraceButton(
                    text = stringResource(id = R.string.your_submissions),
                    onClick = goToSubmission,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                val responseForUserSubmissions by userSubmissionsViewModel.responseForUserSubmissions.collectAsState()
                when (responseForUserSubmissions) {
                    is ApiState.Loading -> {
                        MyCircularProgressIndicator(isDisplayed = true)
                    }
                    is ApiState.Failure -> {
                        FailureScreen(
                            onClickRetry = userSubmissionsViewModel::refreshUserSubmission
                        )
                    }
                    is ApiState.Success -> {
                        val questionCountArray: Array<Int> = Array(9) { 0 }
                        userSubmissionsViewModel.correctProblems.forEach {
                            when (it.first.rating) {
                                in 800..1199 -> questionCountArray[0]++
                                in 1200..1399 -> questionCountArray[1]++
                                in 1400..1599 -> questionCountArray[2]++
                                in 1600..1899 -> questionCountArray[3]++
                                in 1900..2099 -> questionCountArray[4]++
                                in 2100..2399 -> questionCountArray[5]++
                                in 2400..3500 -> questionCountArray[6]++
                                else -> questionCountArray[7]++
                            }
                        }
                        questionCountArray[8] = userSubmissionsViewModel.incorrectProblems.size

                        BarGraphNoOfQueVsRatings(
                            questionCountArray = questionCountArray
                        )
                    }
                }
            }
        }
    }
}

