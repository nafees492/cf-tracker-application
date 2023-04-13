package com.gourav.competrace.app_core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceButton
import com.gourav.competrace.app_core.util.UiText

@Composable
fun FailureScreen(
    onClickRetry: () -> Unit,
    errorMessage: UiText = UiText.StringResource(R.string.something_went_wrong)
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_error_48px),
            contentDescription = stringResource(R.string.error),
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp)
        )
        Text(
            text = errorMessage.asString(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        CompetraceButton(
            text = stringResource(R.string.retry),
            onClick = onClickRetry,
        )
    }
}