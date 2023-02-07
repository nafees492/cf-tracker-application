package com.gourav.competrace.ui.screens

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.data.UserPreferences
import com.gourav.competrace.model.User
import com.gourav.competrace.retrofit.util.ApiState
import com.gourav.competrace.ui.components.BarGraphNoOfQueVsRatings
import com.gourav.competrace.ui.components.CircularProgressIndicator
import com.gourav.competrace.ui.components.NormalButton
import com.gourav.competrace.utils.getRatingTextColor
import com.gourav.competrace.viewmodel.MainViewModel
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
    userPreferences: UserPreferences,
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
                        +CircularRevealPlugin(duration = 300)
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
                        color = getRatingTextColor(rating = user.rating),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (fullName != user.handle) Text(
                        text = user.handle,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = getRatingTextColor(rating = user.rating),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Rating: ${user.rating}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = getRatingTextColor(rating = user.rating)
                    )

                    Text(
                        text = "Max Rating: ${user.maxRating}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = getRatingTextColor(rating = user.maxRating)
                    )

                }
            }
        }

        item {
            NormalButton(
                text = "Your Submissions", onClick = goToSubmission,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
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
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val apiResultForUserSubmission = mainViewModel.responseForUserSubmissions) {
                    is ApiState.Loading -> {
                        CircularProgressIndicator(isDisplayed = true)
                    }
                    is ApiState.Success<*> -> {
                        if (apiResultForUserSubmission.response.status == "OK") {

                            val questionCount: Array<Int> = Array(9) { 0 }
                            mainViewModel.correctProblems.forEach {
                                when (it.first.rating) {
                                    in 800..1199 -> questionCount[0]++
                                    in 1200..1399 -> questionCount[1]++
                                    in 1400..1599 -> questionCount[2]++
                                    in 1600..1899 -> questionCount[3]++
                                    in 1900..2099 -> questionCount[4]++
                                    in 2100..2399 -> questionCount[5]++
                                    in 2400..3500 -> questionCount[6]++
                                    else -> questionCount[7]++
                                }
                            }
                            questionCount[8] = mainViewModel.incorrectProblems.size

                            BarGraphNoOfQueVsRatings(
                                questionCount = questionCount
                            )

                        } else {
                            mainViewModel.responseForUserSubmissions = ApiState.Failure(Throwable())
                        }
                    }
                    is ApiState.Failure -> {
                        NetworkFailScreen(
                            onClickRetry = {
                                mainViewModel.requestForUserSubmission(
                                    userPreferences = userPreferences,
                                    isForced = true
                                )
                            }
                        )
                    }
                    is ApiState.Empty -> {
                        mainViewModel.requestForUserSubmission(
                            userPreferences = userPreferences,
                            isForced = false
                        )
                    }
                    else -> {}
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

