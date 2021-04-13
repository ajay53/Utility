package com.goazi.utility.background

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.goazi.utility.R
import com.goazi.utility.background.captureImage.CameraService
import com.goazi.utility.misc.Constant
import com.goazi.utility.view.activity.NavigationActivity

class BackgroundService : Service() {
    companion object {
        private const val TAG = "BackgroundService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")

        val notificationIntent =
            Intent().setClass(applicationContext, NavigationActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(
            applicationContext,
            Constant.DEFAULT_CHANNEL_ID
        )
            .setContentTitle("Test Title")
            .setContentText("Test Text")
            .setSmallIcon(R.drawable.ic_menu_camera)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(Constant.DEFAULT_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}