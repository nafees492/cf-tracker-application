package com.gourav.competrace.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetenceConfirmationDialog

@Composable
fun AlertClearAllSetNotifications(
    isOpen: Boolean,
    onClickConfirmButton: () -> Unit,
    dismissDialog: () -> Unit
) {
    CompetenceConfirmationDialog(
        isDialogOpen = isOpen,
        title = stringResource(id = R.string.clear_all_set_notif),
        description = stringResource(R.string.desc_clear_all_notif),
        confirmButtonText = stringResource(R.string.yes),
        onClickConfirmButton = onClickConfirmButton,
        dismissButtonText = stringResource(R.string.no),
        dismissDialog = dismissDialog
    )
}