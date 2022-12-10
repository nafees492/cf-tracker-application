package com.example.cfprogresstracker.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.model.Problem
import com.example.cfprogresstracker.model.Submission
import com.example.cfprogresstracker.utils.Verdict
import com.example.cfprogresstracker.utils.loadUrl
import com.example.cfprogresstracker.utils.unixToDateAndTime

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SubmissionCard(
    problem: Problem,
    submissions: ArrayList<Submission>,
    contestListById: MutableMap<Int, Contest>,
    modifier: Modifier = Modifier,
    selectedChips: Set<String> = emptySet(),
    onClickFilterChip: (String) -> Unit = {}
) {
    val context = LocalContext.current

    Card(
        onClick = {
            loadUrl(context = context, url = problem.getLinkViaContest())
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        backgroundColor =  MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                text = "${problem.index}. ${problem.name}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
            )
            Divider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            problem.contestId?.let {
                Text(
                    text = "Contest: ${contestListById[it]?.name}",
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Text(
                text = "Last Submission: ${unixToDateAndTime(submissions[0].creationTimeInMillis())}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            val status = if(problem.hasVerdictOK) Verdict.OK else submissions[0].verdict

            Text(
                text = "Status: $status",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Text(
                text = "Submissions: ${submissions.size}",
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            problem.rating?.let {
                Text(
                    text = "Rating: ${problem.rating}",
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(8.dp)
                )
            }

            problem.tags?.let { tags->
                FilterChipRow(
                    chipList = tags,
                    selectedChips = selectedChips,
                    onClickFilterChip = onClickFilterChip
                )
            }
        }
    }
}