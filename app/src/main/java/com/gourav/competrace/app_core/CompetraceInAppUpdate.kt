package com.gourav.competrace.app_core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarDuration
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.gourav.competrace.app_core.ui.MainActivity
import com.gourav.competrace.app_core.util.SnackbarManager
import com.gourav.competrace.app_core.util.UiText

class CompetraceInAppUpdate(context: Context) {

    private val appContext = context.applicationContext
    private val activity = appContext as Activity
    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(appContext)
    private val updateType = AppUpdateType.FLEXIBLE

    @SuppressLint("SwitchIntDef")
    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        when(state.installStatus()){
            InstallStatus.DOWNLOADED -> {
                SnackbarManager.showMessageWithAction(
                    message = UiText.DynamicString("Update Downloaded"),
                    actionLabel = UiText.DynamicString("Restart"),
                    action = {
                        appUpdateManager.completeUpdate()
                    },
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    init {
        if(updateType == AppUpdateType.FLEXIBLE){
            appUpdateManager.registerListener(installStateUpdatedListener)
        }
    }

    private fun checkForAppUpdates(){
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->

            val isUpdateAvailable = info.updateAvailability() ==  UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllow = info.isUpdateTypeAllowed(updateType)

            if(isUpdateAvailable && isUpdateAllow){
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateType,
                    activity,
                    APP_UPDATE_REQ_CODE
                )
            }
        }
    }



    fun onActivityResult(requestCode: Int, resultCode: Int){
        if(requestCode == APP_UPDATE_REQ_CODE){
            if(resultCode != ComponentActivity.RESULT_OK){
                Log.e(TAG, "onActivityResult: Something Went Wrong.")
            }
        }
    }

    fun onResume(){
        if (updateType == AppUpdateType.IMMEDIATE){
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                val isDevTriggered = info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                if(isDevTriggered){
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        updateType,
                        activity,
                        APP_UPDATE_REQ_CODE
                    )
                }
            }
        }
    }

    fun onDestroy(){
        if(updateType == AppUpdateType.FLEXIBLE){
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }

    companion object{
        private const val TAG = "In App Update"
        private const val APP_UPDATE_REQ_CODE = 3110
    }

}