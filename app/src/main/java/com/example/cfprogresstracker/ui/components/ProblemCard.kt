package com.example.cfprogresstracker.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.model.Problem
import com.example.cfprogresstracker.utils.getRatingGradientColor
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

    val gradientColors = getRatingGradientColor(rating = problem.rating)

    val gradientBrush = Brush.radialGradient(
        0.0f to gradientColors[1],
        1f to gradientColors[0],
        radius = 800f
    )

    Card(
        onClick = {
            loadUrl(context = context, url = problem.getLinkViaProblemSet())
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        // backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        // contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        contentColor = contentColorFor(backgroundColor = gradientColors[1])
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
        ){
            Column(modifier = Modifier.padding(4.dp)) {
                Text(
                    text = "${problem.index}. ${problem.name}",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
                )

                problem.contestId?.let {
                    Text(
                        text = "Contest: ${contestListById[it]?.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                problem.tags?.let { tags->
                    FilterChipRow(
                        chipList = tags,
                        selectedChips = selectedChips,
                        onClickFilterChip = onClickFilterChip
                    )
                }

                problem.rating?.let {
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End){
                        Text(
                            text = "Rating: $it",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }

            }
        }

    }
}