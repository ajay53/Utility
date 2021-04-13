package com.goazi.utility.misc

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.goazi.utility.misc.Constant.Companion.DEFAULT_CHANNEL_ID

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val serviceChannel =
            NotificationChannel(
                DEFAULT_CHANNEL_ID,
                DEFAULT_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )

        val manager: NotificationManager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }
}