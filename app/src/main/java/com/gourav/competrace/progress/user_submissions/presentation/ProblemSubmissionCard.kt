package com.gourav.competrace.progress.user_submissions.presentation

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.FilterChipScrollableRow
import com.gourav.competrace.app_core.ui.theme.BlueTickColor
import com.gourav.competrace.app_core.util.CardValues
import com.gourav.competrace.app_core.util.Verdict
import com.gourav.competrace.app_core.util.copyTextToClipBoard
import com.gourav.competrace.app_core.util.unixToDMYE
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.problemset.model.CodeforcesProblem
import com.gourav.competrace.progress.user_submissions.model.Submission
import com.gourav.competrace.ui.components.BackgroundDesignArrow
import com.gourav.competrace.utils.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemSubmissionCard(
    codeforcesProblem: CodeforcesProblem,
    submissions: ArrayList<Submission>,
    codeforcesContestListById: Map<Any, CompetraceContest>,
    showTags: Boolean,
    onClick: () -> Unit,
    selectedChips: Set<String>,
    onClickFilterChip: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    val ratingContainerColor = getRatingContainerColor(rating = codeforcesProblem.rating)
    val codeforcesContest = codeforcesProblem.contestId?.let { codeforcesContestListById[it] }

    ProblemSubmissionCardDesign(
        rating = codeforcesProblem.rating,
        color = ratingContainerColor,
        onClick = onClick,
        onLongClick = {
            Log.d("Copy URL", codeforcesProblem.toString())
            context.copyTextToClipBoard(
                textToCopy = codeforcesProblem.getLinkViaContest(),
                toastMessageId = R.string.problem_link_copied,
                clipboardManager = clipboardManager,
                haptic = haptic
            )
        },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${codeforcesProblem.index}. ${codeforcesProblem.name}",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            submissions[0].contestId?.let { id ->
                val contest = codeforcesContestListById[id]
                contest?.let {
                    if (submissions[0].isSubmittedDuringContest(it)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_blue_tick),
                            contentDescription = stringResource(R.string.cd_blue_tick),
                            modifier = Modifier
                                .requiredSize(16.dp),
                            tint = BlueTickColor
                        )
                    }
                }
            }
        }


        codeforcesProblem.contestId?.let {
            Text(
                text = "${codeforcesContest?.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = "Last Submission: ${unixToDMYE(submissions[0].creationTimeInMillis())}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val status = if (codeforcesProblem.hasVerdictOK) Verdict.OK else submissions[0].verdict

        val statusColor = getVerdictColor(
            lastVerdict = submissions[0].verdict,
            hasVerdictOK = codeforcesProblem.hasVerdictOK
        )

        val statusAndSubmissions = buildAnnotatedString {
            withStyle(style = SpanStyle(color = statusColor)){
                append(status)
            }
            append("  |  ")
            append("${submissions.size}" + " Submission" + if (submissions.size > 1) "s" else "")
        }

        Text(
            text = statusAndSubmissions,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        codeforcesProblem.tags?.let { tags ->
            if (showTags) FilterChipScrollableRow(
                chipList = tags,
                selectedChips = selectedChips,
                onClickFilterChip = onClickFilterChip
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProblemSubmissionCardDesign(
    rating: Int?,
    color: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(
                getCardHeight(
                    titleLargeTexts = 1,
                    bodyMediumTexts = 3,
                    extraPaddingValues = FilterChipDefaults.Height + 32.dp
                )
            )
            .animateContentSize()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1 - CardValues.TriangularFractionOfCard)
                    .fillMaxHeight(),
                content = content
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(
                        getTextWidthInDp(
                            testSize = MaterialTheme.typography.bodyMedium.fontSize,
                            letters = 4
                        )
                    )
                    .fillMaxHeight()
            ) {
                BackgroundDesignArrow(color = color)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "$rating",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
