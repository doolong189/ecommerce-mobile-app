package com.freshervnc.ecommerceapplication.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.ui.notification.NotificationActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {
    var badgeCount = 0
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notification = message.notification
        val activityIntent = Intent(applicationContext, NotificationActivity::class.java)
        val pendingFlags: Int = if (Build.VERSION.SDK_INT >= 35) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0,activityIntent, pendingFlags)
        sendNotification(notification!!.title,notification.body,pendingIntent)
    }

    private fun sendNotification(title: String?, messageBody: String? ,pendingIntent : PendingIntent) {
        val channelId = "1"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (badgeCount > 0) {
            notificationManager.cancelAll()
            badgeCount += 1
            notificationBuilder.setNumber(badgeCount)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}