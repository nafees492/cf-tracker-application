package com.theruralguys.competrace.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theruralguys.competrace.R
import com.theruralguys.competrace.retrofit.util.ApiState
import com.theruralguys.competrace.ui.components.BarGraphNoOfQueVsRatings
import com.theruralguys.competrace.ui.components.CircularIndeterminateProgressBar
import com.theruralguys.competrace.ui.components.NormalButton
import com.theruralguys.competrace.utils.getRatingTextColor
import com.theruralguys.competrace.utils.processSubmittedProblemFromAPIResult
import com.theruralguys.competrace.viewmodel.MainViewModel

@Composable
fun DummyProgressScreen(
    goToSubmission: () -> Unit,
    mainViewModel: MainViewModel,
    requestForUserSubmission: () -> Unit,
    toggleRequestedForUserSubmissionTo: (Boolean) -> Unit
) {
    val context = LocalContext.current

    val fullName = "User Name"
    val handle = "user_handle"
    val rating = 3011
    val maxRating = 3110

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
                    Image(
                        // CoilImage, FrescoImage
                        painter = painterResource(id = R.drawable.img_avatar),
                        contentDescription = "User Avatar",
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(4.dp)),
                    )
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = fullName,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = getRatingTextColor(rating = rating),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text =  handle,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = getRatingTextColor(rating = rating),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Rating: $rating",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = getRatingTextColor(rating = rating)
                        )

                        Text(
                            text = "Max Rating: $maxRating",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = getRatingTextColor(rating = maxRating)
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
                            processSubmittedProblemFromAPIResult(
                                mainViewModel = mainViewModel,
                                apiResult = apiResultForUserSubmission
                            )
                            val questionCount: Array<Int> = arrayOf(228, 119, 150, 309, 202, 233, 577, 60)
                            val heightsForBarGraph: Array<Int> = Array(8) { 0 }
                            val maxQuestionCount = questionCount.max()
                            val stepSizeOfGraph = (maxQuestionCount / 10 + 1)
                            if(maxQuestionCount != 0) questionCount.forEachIndexed { it, count ->
                                heightsForBarGraph[it] = (count * 500) / (10 * stepSizeOfGraph)
                            }

                            BarGraphNoOfQueVsRatings(
                                heights = heightsForBarGraph,
                                questionCount = questionCount,
                                stepSizeOfGraph = stepSizeOfGraph
                            )

                        } else {
                            mainViewModel.responseForUserSubmissions = ApiState.Failure(Throwable())
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

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}