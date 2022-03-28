package com.example.cfprogresstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.cfprogresstracker.R

@Composable
fun NetworkFailScreen(onClickRetry: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_outline_signal_wifi_bad_24),
            contentDescription = "Network Error",
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp)
        )
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Bad Network Connection. ")
            ClickableText(
                text = AnnotatedString("Retry!"),
                onClick = onClickRetry,
                style = LocalTextStyle.current,

                )
        }
    }
}