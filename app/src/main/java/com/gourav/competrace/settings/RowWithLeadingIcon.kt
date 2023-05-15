package com.gourav.competrace.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RowWithLeadingIcon(
    title: String,
    leadingIconId: Int,
    onClick: () -> Unit,
    subTitle: String? = null,
    switchState: Boolean? = null,
    showError: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = leadingIconId), contentDescription = null,
            modifier = Modifier.padding(start = 16.dp, end = 24.dp),
            tint = if(showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                subTitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            switchState?.let {
                Switch(
                    checked = it,
                    onCheckedChange = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    }
}