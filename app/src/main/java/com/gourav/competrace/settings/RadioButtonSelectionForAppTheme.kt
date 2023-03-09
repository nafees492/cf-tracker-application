package com.gourav.competrace.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun RadioButtonSelectionForAppTheme(
    themeOptions: List<String>,
    isOptionSelected: (String) -> Boolean,
    onClickOption: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.selectableGroup(),
    ) {
        themeOptions.forEach { theme ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(2.dp))
                    .selectable(
                        selected = isOptionSelected(theme),
                        onClick = { onClickOption(theme) },
                        role = Role.RadioButton
                    )
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isOptionSelected(theme),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = theme,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}