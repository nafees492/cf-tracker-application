package com.example.cfprogresstracker.ui.screens

import androidx.compose.foundation.layout.*
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
import coil.compose.AsyncImage
import com.example.cfprogresstracker.R
import com.example.cfprogresstracker.model.User
import com.example.cfprogresstracker.retrofit.util.ApiState
import com.example.cfprogresstracker.ui.components.BarGraph
import com.example.cfprogresstracker.ui.components.CircularIndeterminateProgressBar
import com.example.cfprogresstracker.ui.components.NormalButton
import com.example.cfprogresstracker.ui.navigation.navsections.processSubmittedProblem
import com.example.cfprogresstracker.ui.theme.*
import com.example.cfprogresstracker.viewmodel.MainViewModel

@Composable
fun ProgressScreen(
    user: User,
    goToSubmission: () -> Unit,
    mainViewModel: MainViewModel,
    requestForUserSubmission: () -> Unit,
    toggleRequestedForUserSubmissionTo: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user.titlePhoto,
                    contentDescription = null,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                )
                Column(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()) {
                    var fullName = ""
                    user.firstName?.let { fullName = fullName.plus("$it ") }
                    user.lastName?.let { fullName = fullName.plus(it) }

                    Text(
                        text = fullName,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 4.dp),
                        color = user.ratingCategory.color
                    )
                    Text(
                        text = user.handle,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        color = user.ratingCategory.color
                    )

                    Text(
                        text = "Current Rating: ${user.rating}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                    )

                    Text(
                        text = "Maximum Rating: ${user.maxRating}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                    )

                }
            }
        }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp),
            contentAlignment = Alignment.Center
        ){
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
                            when(it.first.rating){
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
                        questionCount.forEachIndexed{ it, count ->
                            heightsForBarGraph[it] = (count * 750) / maxQuestionCount
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
                            questionCount = questionCount
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

