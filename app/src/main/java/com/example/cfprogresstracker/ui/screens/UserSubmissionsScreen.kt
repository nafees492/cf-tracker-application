package com.example.cfprogresstracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.contentColorFor
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfprogresstracker.model.Problem
import com.example.cfprogresstracker.model.Submission

@Composable
fun UserSubmissionScreen(submittedProblems: List<Pair<Problem, List<Submission>>>) {
    LazyColumn {
        items(submittedProblems.size) {
            SubmissionCard(problemAndItsSubmission = submittedProblems[it])
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SubmissionCard(problemAndItsSubmission: Pair<Problem, List<Submission>>) {
    val problem = remember {
        problemAndItsSubmission.first
    }
    val submission = remember {
        problemAndItsSubmission.second
    }
    val bgColor =
        if (submission[0].verdict == "OK") MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surface

    Card(
        onClick = { /*TODO*/ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        backgroundColor = bgColor,
        contentColor = contentColorFor(backgroundColor = bgColor)
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
            Text(
                text = "Status: ${submission[0].verdict}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = "Submissions: ${submission.size}",
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            if (problem.rating != null) {
                Text(
                    text = "Rating: ${problem.rating}",
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}