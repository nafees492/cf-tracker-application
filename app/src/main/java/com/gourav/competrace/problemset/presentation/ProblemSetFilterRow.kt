package com.gourav.competrace.problemset.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceBadgeIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemSetFilterRow(
    showRatingSheet: () -> Unit,
    showTagSheet: () -> Unit,
    ratingRange: IntRange,
    selectedTags: Set<String>,
    isTagsVisible: Boolean
) {

    val tagString = buildString {
        if (selectedTags.isEmpty()) append("None")
        selectedTags.forEach { tag ->
            append(tag)
            if (tag != selectedTags.last()) append(", ")
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Card(
            onClick = showRatingSheet,
            modifier = Modifier.weight(1f),
            colors = CardDefaults.elevatedCardColors()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Rating",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${ratingRange.first} - ${ratingRange.last}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            ContentAlpha.medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BadgedBox(
                    badge = { if(ratingRange != 800..3500) Badge()},
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_drop_down_24px),
                        contentDescription = null,
                    )
                }
            }
        }

        Card(
            onClick = showTagSheet,
            modifier = Modifier.weight(1f),
            colors = CardDefaults.elevatedCardColors(
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            ),
            enabled = isTagsVisible
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tags",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = tagString,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            ContentAlpha.medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BadgedBox(
                    badge = { if(selectedTags.isNotEmpty()) Badge{ Text(text = selectedTags.size.toString()) }},
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_drop_down_24px),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}