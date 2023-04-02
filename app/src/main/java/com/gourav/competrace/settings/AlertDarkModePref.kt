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
fun AlertDarkModePref(
    isOpen: Boolean,
    darkModePrefOptions: List<String>,
    selectedOption: String,
    onSelectOption: (String) -> Unit,
    dismissDialog: () -> Unit
) {
    CompetraceAlertDialog(
        openDialog = isOpen,
        iconId = R.drawable.ic_dark_mode_24px,
        title = stringResource(id = R.string.dark_mode_pref),
        confirmButtonText = stringResource(id = R.string.ok),
        onClickConfirmButton = dismissDialog,
        dismissDialog = dismissDialog
    ) {
        LazyColumn {
            item {
                RadioButtonSelection(
                    options = darkModePrefOptions,
                    isOptionSelected = { selectedOption == it},
                    onClickOption = onSelectOption
                )
            }
        }
    }
}