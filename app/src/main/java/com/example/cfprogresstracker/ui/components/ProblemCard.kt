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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.model.Problem
import com.example.cfprogresstracker.utils.loadUrl

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProblemCard(
    problem: Problem,
    contestListById: MutableMap<Int, Contest>,
    modifier: Modifier = Modifier,
    selectedChips: Set<String> = emptySet(),
    onClickFilterChip: (String) -> Unit = {}
) {

    val context = LocalContext.current

    Card(
        onClick = {
            loadUrl(context = context, url = problem.getLinkViaProblemSet())
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                text = "${problem.index}. ${problem.name}",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
            )
            Divider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            problem.contestId?.let {
                Text(
                    text = "Contest: ${contestListById[it]?.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp)
                )
            }

            problem.rating?.let {
                Text(
                    text = "Rating: $it",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
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