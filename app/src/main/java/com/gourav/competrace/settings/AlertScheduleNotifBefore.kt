package com.gourav.competrace.settings

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceAlertDialog
import com.gourav.competrace.settings.util.ScheduleNotifBeforeOptions

@Composable
fun AlertScheduleNotifBefore(
    alarmManager: AlarmManager,
    isOpen: Boolean,
    selectedOption: Int,
    onSelectOption: (Int) -> Unit,
    dismissDialog: () -> Unit
) {
    val context = LocalContext.current
    val options = remember { ScheduleNotifBeforeOptions.values().map { it.option } }

    CompetraceAlertDialog(
        openDialog = isOpen,
        iconId = R.drawable.ic_schedule_24px,
        title = stringResource(id = R.string.schedule_notif_before),
        confirmButtonText = stringResource(id = R.string.ok),
        onClickConfirmButton = dismissDialog,
        dismissDialog = dismissDialog
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                RadioButtonSelection(
                    options = options,
                    isOptionSelected = { option ->
                        ScheduleNotifBeforeOptions.getValue(option) == selectedOption
                    },
                    onClickOption = { option ->
                        ScheduleNotifBeforeOptions.getValue(option).let(onSelectOption)
                    }
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) item {
                    Column {
                        Text(
                            text = stringResource(R.string.exact_alarm_request),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                        TextButton(onClick = {
                            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }.also(context::startActivity)

                        }) {
                            Text(
                                text = stringResource(id = R.string.go_to_settings),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.note_schedule_notif_before),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}