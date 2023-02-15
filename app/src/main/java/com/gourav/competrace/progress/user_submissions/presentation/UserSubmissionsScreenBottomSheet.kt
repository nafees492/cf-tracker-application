package com.gourav.competrace.progress.user_submissions.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gourav.competrace.contests.model.Contest
import com.gourav.competrace.problemset.model.Problem
import com.gourav.competrace.progress.user_submissions.model.Submission
import com.gourav.competrace.ui.components.BottomSheetDragIndicator
import com.gourav.competrace.ui.components.CompetraceClickableText
import com.gourav.competrace.utils.copyTextToClipBoard
import com.gourav.competrace.utils.loadUrl

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserSubmissionsScreenBottomSheetContent(
    problem: Problem?,
    submissions: ArrayList<Submission>?,
    contest: Contest?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val hapticFeedback = LocalHapticFeedback.current
    val linkEmoji = "\uD83D\uDD17"

    BottomSheetDragIndicator()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        CompetraceClickableText(
            text = AnnotatedString(
                text = "${problem?.index}. ${problem?.name}",
                spanStyle = SpanStyle(textDecoration = TextDecoration.Underline)
            ),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            onClick = {
                loadUrl(context = context, url = problem?.getLinkViaContest())
            },
            onLongClick = {
                copyTextToClipBoard(
                    context = context,
                    text = problem?.getLinkViaContest(),
                    toastMessage = "Problem Link Copied",
                    clipboardManager = clipboardManager,
                    haptic = hapticFeedback
                )
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        CompetraceClickableText(
            text = AnnotatedString(
                text = contest?.name.toString(),
                spanStyle = SpanStyle(textDecoration = TextDecoration.Underline)
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            onClick = {
                loadUrl(context = context, url = contest?.getLink())
            },
            onLongClick = {
                copyTextToClipBoard(
                    context = context,
                    text = contest?.getLink(),
                    toastMessage = "Contest Link Copied",
                    clipboardManager = clipboardManager,
                    haptic = hapticFeedback
                )
            }
        )
    }

    submissions?.let { list ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            items(list.size) {
                IndividualSubmissionCard(submission = list[it])
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
