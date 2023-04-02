package com.gourav.competrace.progress.user.presentation.login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceIconButton

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserCard(
    handle: String,
    onClick: (String) -> Unit,
    onLongClick: () -> Unit,
    onClickCancelIcon: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .combinedClickable(
                onClick = { onClick(handle) },
                onLongClick = onLongClick,
            ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = handle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
            CompetraceIconButton(
                iconId = R.drawable.ic_close_24px,
                onClick = { onClickCancelIcon(handle) }
            )
        }
    }
}