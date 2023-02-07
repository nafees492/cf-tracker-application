package com.gourav.competrace.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability


class InAppUpdate(activity: Activity) : InstallStateUpdatedListener {

    private var appUpdateManager: AppUpdateManager
    private var parentActivity: Activity = activity

    private var currentType = AppUpdateType.FLEXIBLE

    init {
        appUpdateManager = AppUpdateManagerFactory.create(parentActivity)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            // Check if update is available
            Log.d(TAG, "Checking Update")
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) { // UPDATE IS AVAILABLE
                Log.d(TAG, "Update Available: Version Code ${info.availableVersionCode()}")
                startUpdate(info, AppUpdateType.FLEXIBLE)
            } else {
                // UPDATE IS NOT AVAILABLE
                Log.d(TAG, "Update Not Available")
            }
        }
        appUpdateManager.registerListener(this)
    }


    private fun startUpdate(info: AppUpdateInfo, type: Int) {
        Log.d(TAG, "Update Download Start Flow Launched")
        appUpdateManager.startUpdateFlowForResult(info, type, parentActivity, MY_REQUEST_CODE)
        currentType = type
    }

    fun onResume() {
        Log.d(TAG, "Activity Resumed")
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (currentType == AppUpdateType.FLEXIBLE) {
                // If the update is downloaded but not installed, notify the user to complete the update.
                if (info.installStatus() == InstallStatus.DOWNLOADED) {
                    Log.d(TAG, "Update Downloaded")
                    appUpdateManager.completeUpdate()
                }
            } else if (currentType == AppUpdateType.IMMEDIATE) {
                // for AppUpdateType.IMMEDIATE only, already executing updater
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    startUpdate(info, AppUpdateType.IMMEDIATE)
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                // If the update is cancelled or fails, you can request to start the update again.
                Log.e("ERROR", "Update flow failed! Result code: $resultCode")
            }
        }
    }

    fun onDestroy() {
        Log.e(TAG, "Activity Destroyed")
        appUpdateManager.unregisterListener(this)
    }

    override fun onStateUpdate(state: InstallState) {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            appUpdateManager.completeUpdate()
        }
    }

    companion object {
        private const val MY_REQUEST_CODE = 3110
        private const val TAG = "In App Update"
    }

}