package com.arl.steamscraper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(context: Context?, private val notificationTitle: String, private val gameInfo: String, private val id: Int) : ContextWrapper(context) {

    private val channelId = "sales_notification"
    private val channelName = "Sales Notification"
    private var manager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    private fun createChannel() {
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lightColor = R.color.steam_blue
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.importance = NotificationManager.IMPORTANCE_HIGH

        getManager().createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager {
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager as NotificationManager
    }

    fun getNotificationChannel(): NotificationCompat.Builder {
        // Intent to open App when clicking on notification
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, id, intent, 0)

        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(notificationTitle)
            .setContentText(gameInfo)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(resources.getColor(R.color.steam_blue, null))
            .setContentIntent(pendingIntent).
            setAutoCancel(true)
    }

}