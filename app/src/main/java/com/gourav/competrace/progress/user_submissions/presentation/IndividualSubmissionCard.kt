package com.gourav.competrace.progress.user_submissions.presentation

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gourav.competrace.app_core.util.unixToDMYETZ
import com.gourav.competrace.progress.user_submissions.model.Submission
import com.gourav.competrace.utils.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IndividualSubmissionCard(
    submission: Submission,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .animateContentSize()
            .combinedClickable(
                onClick = { loadUrl(context = context, url = submission.getLink()) },
                onLongClick = {
                    Log.d("Copy URL", submission.toString())
                    copyTextToClipBoard(
                        text = submission.getLink(),
                        toastMessage = "Submission Link Copied",
                        context = context,
                        clipboardManager = clipboardManager,
                        haptic = haptic
                    )
                },
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = submission.id.toString() + " - ",
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = submission.verdict,
                style = MaterialTheme.typography.labelLarge,
                color = getVerdictColor(
                    lastVerdict = submission.verdict,
                    hasVerdictOK = submission.verdict == Verdict.OK
                )
            )
        }
        Text(
            text = "Passed Test Cases: ${submission.passedTestCount}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
        )
        Text(
            text = submission.programmingLanguage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
        )
        Text(
            text = unixToDMYETZ(submission.creationTimeInMillis()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}