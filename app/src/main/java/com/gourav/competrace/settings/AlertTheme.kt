package com.gourav.competrace.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceAlertDialog

@Composable
fun AlertTheme(
    isOpen: Boolean,
    themeOptions: List<String>,
    selectedOption: String,
    onSelectOption: (String) -> Unit,
    dismissDialog: () -> Unit
) {
    CompetraceAlertDialog(
        openDialog = isOpen,
        iconId = R.drawable.ic_palette_24px,
        title = stringResource(id = R.string.theme),
        confirmButtonText = stringResource(id = R.string.ok),
        onClickConfirmButton = dismissDialog,
        dismissDialog = dismissDialog
    ) {
        LazyColumn {
            item {
                RadioButtonSelection(
                    options = themeOptions,
                    isOptionSelected = { selectedOption == it},
                    onClickOption = onSelectOption
                )
            }
        }
    }
}