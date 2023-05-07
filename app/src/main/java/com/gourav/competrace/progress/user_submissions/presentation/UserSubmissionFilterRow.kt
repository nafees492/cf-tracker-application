package com.gourav.competrace.progress.user_submissions.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gourav.competrace.app_core.ui.components.BulletSpan
import com.gourav.competrace.app_core.util.UiText
import com.gourav.competrace.app_core.util.UserSubmissionFilter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun UserSubmissionFilterRow(
    currentSelection: UiText,
    problemCountOnScreen: Int,
    updateCurrentSelection: (UiText) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(8.dp)
    ){
        items(UserSubmissionFilter.values().size) {
            val filter = UserSubmissionFilter.values()[it]

            TabElement(
                isSelected = currentSelection == filter,
                text = filter.asString(),
                count = problemCountOnScreen,
                onClick = { updateCurrentSelection(filter) },
            )
            if (it != UserSubmissionFilter.values().lastIndex)
                BulletSpan(modifier = Modifier.padding(8.dp))
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TabElement(
    isSelected: Boolean,
    text: String,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(targetState = isSelected) {
        val style =
            if (it) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall
        val titleText =
            if (it) text else text.split(" ").first()
        val color =
            if (it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

        Row(
            modifier = modifier
                .animateContentSize()
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = titleText,
                    style = style,
                    color = color,
                    modifier = Modifier
                        .animateContentSize()
                )
                if (it) Text(
                    text = count.toString(),
                    style = style,
                    color = color,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .animateContentSize()
                )
            }

        }
    }
}