package com.gourav.competrace.progress.user_submissions.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.gourav.competrace.app_core.util.copyTextToClipBoard
import com.gourav.competrace.app_core.util.loadUrl
import com.gourav.competrace.app_core.util.unixToDMYETZ
import com.gourav.competrace.progress.user_submissions.model.Submission
import com.gourav.competrace.utils.*
import com.gourav.competrace.R
import com.gourav.competrace.app_core.util.Verdict

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
                    context.copyTextToClipBoard(
                        textToCopy = submission.getLink(),
                        toastMessageId = R.string.submission_link_copied,
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
        val idAndVerdict = buildAnnotatedString {
            append(submission.id.toString())
            append(" - ")
            withStyle(style = SpanStyle(color = getVerdictColor(
                lastVerdict = submission.verdict,
                hasVerdictOK = submission.verdict == Verdict.OK
            ))){
                append(submission.verdict)
            }
        }

        Text(
            text = idAndVerdict,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 4.dp)
        )

        Text(
            text = stringResource(id = R.string.passes_test_cases, submission.passedTestCount),
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