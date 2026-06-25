package com.babymomo.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BabymomoApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val heartbeatChannel = NotificationChannel(
                CHANNEL_HEARTBEAT,
                "Heartbeat Check-ins",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Autonomous background check-ins from BabyMomo" }

            val toolChannel = NotificationChannel(
                CHANNEL_TOOLS,
                "Tool Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Notifications from BabyMomo tools" }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(heartbeatChannel)
            notificationManager.createNotificationChannel(toolChannel)
        }
    }

    companion object {
        const val CHANNEL_HEARTBEAT = "heartbeat_channel"
        const val CHANNEL_TOOLS = "tools_channel"
    }
}
