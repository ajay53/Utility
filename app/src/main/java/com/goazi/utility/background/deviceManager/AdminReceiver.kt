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

class AdminReceiver : DeviceAdminReceiver() {
    companion object{
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
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
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