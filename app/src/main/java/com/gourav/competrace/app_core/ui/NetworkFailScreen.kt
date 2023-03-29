package com.gourav.competrace.app_core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceButton

@Composable
fun NetworkFailScreen(onClickRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_error_48px),
            contentDescription = "Network Error",
            modifier = Modifier.size(80.dp).padding(8.dp)
        )
        Text(
            text = "Something Went Wrong!",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        CompetraceButton(
            text = "Retry",
            onClick = onClickRetry,
        )
    }
}