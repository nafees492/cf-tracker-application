package com.gourav.competrace.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceAlertDialog
import com.gourav.competrace.app_core.util.SnackbarManager
import com.gourav.competrace.app_core.util.UiText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AlertTestNotif(
    isOpen: Boolean,
    onSelectOk: (Int) -> Unit,
    dismissDialog: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var timeAfter by remember {
        mutableStateOf("")
    }

    CompetraceAlertDialog(
        openDialog = isOpen,
        title = stringResource(id = R.string.test_notif),
        confirmButtonText = stringResource(id = R.string.set),
        onClickConfirmButton = {
            try {
                timeAfter.toInt().let {
                    if(it >= 0) onSelectOk(it)
                    else throw Exception()
                }
                dismissDialog()
            } catch (_: Exception) {
                SnackbarManager.showMessage(UiText.StringResource(R.string.please_enter_int))
            }
        },
        dismissDialog = dismissDialog
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(id = R.string.schedule_notif_after),
                style = MaterialTheme.typography.bodyMedium
            )
            TextField(
                value = timeAfter, onValueChange = { timeAfter = it },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.enter_minutes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(id = R.string.note_test_notif),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
            )
        }
    }
}