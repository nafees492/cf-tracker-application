package com.example.cfprogresstracker.ui.components

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
import com.example.cfprogresstracker.ui.theme.AppTheme

@Composable
fun RadioButtonSelectionForAppTheme(
    themeOptions: ArrayList<AppTheme>,
    isOptionSelected: (AppTheme) -> Boolean,
    onClickOption: (AppTheme) -> Unit
){
    Column(modifier = Modifier.selectableGroup()) {
        themeOptions.forEach { theme ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .selectable(
                        selected = isOptionSelected(theme),
                        onClick = { onClickOption(theme) },
                        role = Role.RadioButton
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isOptionSelected(theme),
                    onClick = null // null recommended for accessibility with screen readers
                )
                Text(
                    text = theme.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}