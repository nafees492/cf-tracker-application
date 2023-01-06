package com.example.cfprogresstracker.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.model.Problem
import com.example.cfprogresstracker.model.Submission
import com.example.cfprogresstracker.utils.*

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

    val isSelected: (String) -> Boolean = { selectedChips.contains(it) }

    val trailingIcon: @Composable ((visible: Boolean) -> Unit) = {
        AnimatedVisibility(visible = it) {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = ""
            )
        }
    }

    val ratingContainerColor = getRatingContainerColor(rating = problem.rating)

    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    ProblemSubmissionCardDesign(
        rating = problem.rating,
        color = ratingContainerColor,
        onClick = {
            loadUrl(context = context, url = problem.getLinkViaContest())
        },
        onLongClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            clipboardManager.setText(AnnotatedString(problem.getLinkViaContest()))
            Toast.makeText(context, "Problem Link Copied", Toast.LENGTH_SHORT).show()
        },
        modifier = modifier
    ) {
        Text(
            text = "${problem.index}. ${problem.name}",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
            modifier = Modifier
                .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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

        Text(
            text = "Last Submission: ${unixToDateAndTime(submissions[0].creationTimeInMillis())}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val status = if (problem.hasVerdictOK) Verdict.OK else submissions[0].verdict

        val statusColor = getVerdictColor(
            lastVerdict = submissions[0].verdict,
            hasVerdictOK = problem.hasVerdictOK
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$status",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = statusColor,
            )
            Text(
                text = "|",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = "${submissions.size}" + " Submission" + if (submissions.size > 1) "s" else "",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        problem.tags?.let { tags ->
            LazyRow(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
            ) {
                items(tags.size) {
                    FilterChip(
                        selected = isSelected(tags[it]),
                        onClick = { onClickFilterChip(tags[it]) },
                        label = {
                            Text(
                                text = tags[it],
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        trailingIcon = { trailingIcon(isSelected(tags[it])) },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        shape = RectangleShape,
                    )
                }
            }
        }
    }
}