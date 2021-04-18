package com.goazi.utility.background.deviceManager

import android.Manifest
import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.UserHandle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.goazi.utility.background.captureImage.CameraService
import com.goazi.utility.misc.Constant.Companion.CAPTURE_IMAGE
import com.goazi.utility.view.activity.NavigationActivity

class AdminReceiver : DeviceAdminReceiver() {
    companion object {
        private const val TAG = "AdminReceiver"
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d(TAG, "onEnabled: ")
    }

    override fun onPasswordSucceeded(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordSucceeded(context, intent, user)
        Log.d(TAG, "onPasswordSucceeded: ")
    }

    override fun onPasswordFailed(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordFailed(context, intent, user)
        Log.d(TAG, "onPasswordFailed: ")
        val isCameraServiceEnabled: Boolean =
            NavigationActivity.preferences.getBoolean(CAPTURE_IMAGE, true)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED && isCameraServiceEnabled
        ) {
            val cameraIntent = Intent()
                .setClass(context, CameraService::class.java)
            context.startService(cameraIntent)
        }
    }

    override fun onPasswordChanged(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordChanged(context, intent, user)
        Log.d(TAG, "onPasswordChanged: ")
    }
}