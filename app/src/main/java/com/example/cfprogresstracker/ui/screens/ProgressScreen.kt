package com.example.cfprogresstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cfprogresstracker.R
import com.example.cfprogresstracker.model.User
import com.example.cfprogresstracker.retrofit.util.ApiState
import com.example.cfprogresstracker.ui.components.BarGraph
import com.example.cfprogresstracker.ui.components.CircularIndeterminateProgressBar
import com.example.cfprogresstracker.ui.components.NormalButton
import com.example.cfprogresstracker.ui.navigation.navsections.processSubmittedProblem
import com.example.cfprogresstracker.ui.theme.*
import com.example.cfprogresstracker.utils.getRatingColor
import com.example.cfprogresstracker.viewmodel.MainViewModel
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@Composable
fun ProgressScreen(
    user: User,
    goToSubmission: () -> Unit,
    mainViewModel: MainViewModel,
    requestForUserSubmission: () -> Unit,
    toggleRequestedForUserSubmissionTo: (Boolean) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlideImage( // CoilImage, FrescoImage
                        imageModel = { user.titlePhoto },
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        component = rememberImageComponent {
                            +CircularRevealPlugin(duration = 350)
                            // shows a shimmering effect when loading an image.
                            +ShimmerPlugin(
                                baseColor = MaterialTheme.colorScheme.surface,
                                highlightColor = MaterialTheme.colorScheme.onSurface
                            )
                            // Failure Image
                            +PlaceholderPlugin.Failure(
                                painterResource(
                                    id = R.drawable.broken_image_48px
                                )
                            )
                        },
                    )
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        var fullName = ""
                        user.firstName?.let { fullName = fullName.plus("$it ") }
                        user.lastName?.let { fullName = fullName.plus(it) }

                        Text(
                            text = fullName,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                            color = getRatingColor(rating = user.rating)
                        )
                        Text(
                            text = user.handle,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp),
                            color = getRatingColor(rating = user.rating)
                        )

                        Text(
                            text = "Current Rating: ${user.rating}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 4.dp),
                            color = getRatingColor(rating = user.rating)
                        )

                        Text(
                            text = "Maximum Rating: ${user.maxRating}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp),
                            color = getRatingColor(rating = user.maxRating)
                        )

                    }
                }
            }
        }

        item {
            NormalButton(
                text = "Your Submissions", onClick = goToSubmission,
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_navigate_next_24px),
                        contentDescription = "",
                    )
                }
            )
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val apiResultForUserSubmission = mainViewModel.responseForUserSubmissions) {
                    is ApiState.Loading -> {
                        CircularIndeterminateProgressBar(isDisplayed = true)
                    }
                    is ApiState.Success<*> -> {
                        if (apiResultForUserSubmission.response.status == "OK") {
                            processSubmittedProblem(
                                mainViewModel = mainViewModel,
                                apiResult = apiResultForUserSubmission
                            )
                            val questionCount: Array<Int> = Array(8) { 0 }
                            mainViewModel.correctProblems.forEach {
                                when (it.first.rating) {
                                    in 800..1199 -> questionCount[0]++
                                    in 1200..1399 -> questionCount[1]++
                                    in 1400..1599 -> questionCount[2]++
                                    in 1600..1899 -> questionCount[3]++
                                    in 1900..2099 -> questionCount[4]++
                                    in 2100..2399 -> questionCount[5]++
                                    in 2400..4000 -> questionCount[6]++
                                }
                            }
                            questionCount[7] = mainViewModel.incorrectProblems.size
                            val heightsForBarGraph: Array<Int> = Array(8) { 0 }
                            val maxQuestionCount = questionCount.max()
                            val stepSizeOfGraph = (maxQuestionCount / 10 + 1)
                            if(maxQuestionCount != 0) questionCount.forEachIndexed { it, count ->
                                heightsForBarGraph[it] = (count * 500) / (10 * stepSizeOfGraph)
                            }

                            val colors = arrayOf(
                                NewbieGray,
                                PupilGreen,
                                SpecialistCyan,
                                ExpertBlue,
                                CandidMasterViolet,
                                MasterOrange,
                                GrandmasterRed,
                                MaterialTheme.colorScheme.onSurface
                            )

                            BarGraph(
                                heights = heightsForBarGraph,
                                colors = colors,
                                questionCount = questionCount,
                                stepSizeOfGraph = stepSizeOfGraph
                            )

                        } else {
                            mainViewModel.responseForUserSubmissions =
                                ApiState.Failure(Throwable())
                        }
                    }
                    is ApiState.Failure -> {
                        NetworkFailScreen(
                            onClickRetry = {
                                toggleRequestedForUserSubmissionTo(false)
                                requestForUserSubmission()
                            }
                        )
                    }
                    is ApiState.Empty -> {
                        requestForUserSubmission()
                    }
                    else -> {
                        // Nothing
                    }
                }
            }
        }
    }
}

