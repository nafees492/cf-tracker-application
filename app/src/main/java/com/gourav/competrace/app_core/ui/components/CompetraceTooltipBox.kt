package com.gourav.competrace.app_core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetraceTooltipBox(
    text: String?,
    tooltipState: RichTooltipState = remember { RichTooltipState() },
    content: @Composable TooltipBoxScope.() -> Unit
) {
    RichTooltipBox(
        tooltipState = tooltipState,
        text = {
            text?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(4.dp),
                    softWrap = false,
                )
            }
        },
        modifier = Modifier.padding(horizontal = 8.dp),
        colors = TooltipDefaults.richTooltipColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        content = content
    )
}