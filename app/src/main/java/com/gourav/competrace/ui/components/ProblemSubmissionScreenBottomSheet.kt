package com.gourav.competrace.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gourav.competrace.model.Contest
import com.gourav.competrace.model.Problem
import com.gourav.competrace.model.Submission
import com.gourav.competrace.utils.copyTextToClipBoard
import com.gourav.competrace.utils.loadUrl

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProblemSubmissionScreenBottomSheetContent(
    problem: Problem?,
    submissions: ArrayList<Submission>?,
    contest: Contest?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val hapticFeedback = LocalHapticFeedback.current
    val linkEmoji = "\uD83D\uDD17"

    Surface(
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        tonalElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            BottomSheetDragIndicator()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                NormalClickableText(
                    text = AnnotatedString(linkEmoji + " " + "${problem?.index}. ${problem?.name}"),
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
                NormalClickableText(
                    text = AnnotatedString( linkEmoji + " " + contest?.name.toString()),
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
                LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                    items(list.size) {
                        SubmissionCard(submission = list[it])
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}