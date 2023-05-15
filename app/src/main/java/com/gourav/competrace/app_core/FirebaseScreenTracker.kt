package com.gourav.competrace.app_core

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.gourav.competrace.app_core.util.Screens

@Composable
fun TrackScreen(
    screen: Screens,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val TAG = "Track Screen"
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                    param(FirebaseAnalytics.Param.SCREEN_NAME, screen.screenName)
                }
                Log.d(TAG, "Started - " + screen.screenName)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            Log.d(TAG, "Stopped - " + screen.screenName)
        }
    }
}