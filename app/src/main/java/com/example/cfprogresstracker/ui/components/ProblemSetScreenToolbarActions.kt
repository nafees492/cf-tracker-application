package com.example.cfprogresstracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cfprogresstracker.R
import com.example.cfprogresstracker.utils.ProblemSetFilter

@ExperimentalAnimationApi
@Composable
fun RowScope.ProblemSetScreenActions(
    currentSelectionForProblemSet: String,
    onClickAll: () -> Unit,
    onClick: ArrayList<() -> Unit>
) {
    var expanded by remember { mutableStateOf(false) }

    val trailingIcon: @Composable ((visible: Boolean) -> Unit) = {
        AnimatedVisibility(visible = it) {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = ""
            )
        }
    }

    IconButton(onClick = {
        expanded = true
    }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_filter_list_24px),
            contentDescription = "",
        )
    }
    AnimatedVisibility(visible = expanded) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {

            DropdownMenuItem(
                text = { Text(ProblemSetFilter.ALL) },
                onClick = onClickAll,
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.ALL) }
            )
            Text(
                text = "Ratings",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(8.dp)
            )
            Divider()
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[0]) },
                onClick = onClick[0],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[0]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[1]) },
                onClick = onClick[1],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[1]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[2]) },
                onClick = onClick[2],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[2]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[3]) },
                onClick = onClick[3],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[3]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[4]) },
                onClick = onClick[4],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[4]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[5]) },
                onClick = onClick[5],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[5]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[6]) },
                onClick = onClick[6],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[6]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[7]) },
                onClick = onClick[7],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[7]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[8]) },
                onClick = onClick[8],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[8]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[9]) },
                onClick = onClick[9],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[9]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[10]) },
                onClick = onClick[10],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[10]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[11]) },
                onClick = onClick[11],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[11]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[12]) },
                onClick = onClick[12],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[12]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[13]) },
                onClick = onClick[13],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[13]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[14]) },
                onClick = onClick[14],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[14]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[15]) },
                onClick = onClick[15],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[15]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[16]) },
                onClick = onClick[16],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[16]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[17]) },
                onClick = onClick[17],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[17]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[18]) },
                onClick = onClick[18],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[18]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[19]) },
                onClick = onClick[19],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[19]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[20]) },
                onClick = onClick[20],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[20]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[21]) },
                onClick = onClick[21],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[21]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[22]) },
                onClick = onClick[22],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[22]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[23]) },
                onClick = onClick[23],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[23]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[24]) },
                onClick = onClick[24],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[24]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[25]) },
                onClick = onClick[25],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[25]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[26]) },
                onClick = onClick[26],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[26]) })
            DropdownMenuItem(
                text = { Text(ProblemSetFilter.RATING[27]) },
                onClick = onClick[27],
                trailingIcon = { trailingIcon(currentSelectionForProblemSet == ProblemSetFilter.RATING[27]) })
        }
    }
}

