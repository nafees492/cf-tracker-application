package com.example.cfprogresstracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cfprogresstracker.model.User
import com.example.cfprogresstracker.ui.components.NormalButton

@Composable
fun ProgressScreen(
    user: User,
    goToSubmission: () -> Unit,
    onClickLogoutBtn: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Welcome " + user.handle, modifier = Modifier.padding(8.dp))
        Text(text = "Your Rating " + user.rating, modifier = Modifier.padding(8.dp))
        NormalButton(
            text = "Your Submissions", onClick = goToSubmission,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
        NormalButton(text = "Logout", onClick = onClickLogoutBtn, modifier = Modifier.padding(8.dp))
    }
}