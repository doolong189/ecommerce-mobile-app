package com.freshervnc.ecommerceapplication.common.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.freshervnc.ecommerceapplication.utils.SocketIOManager
import com.google.firebase.FirebaseApp

class MyApplication : Application() {

    companion object {
        const val CHANNEL_ID = "CHANNEL_EXAMPLE"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        FirebaseApp.initializeApp(this)
        SocketIOManager()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel Example"
            val descriptionText = "Example Service Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}