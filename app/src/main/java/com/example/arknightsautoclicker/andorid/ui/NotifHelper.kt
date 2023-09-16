package com.example.arknightsautoclicker.andorid.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat

/**
 * helper class for creating notifications
 */
open class NotifHelper(
    protected val ctx: Context
) {
    companion object {
        const val CHANNEL_ID = "notification_channel"
        const val CHANNEL_NAME = "notification_channel_name"
    }
    val mgr = ctx.getSystemService(NotificationManager::class.java)
    fun createNotificationChannel() {
        if (mgr.getNotificationChannel(CHANNEL_ID) == null) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            mgr.createNotificationChannel(channel)
        }
    }

    fun createNotification(title: String, icon: IconCompat, desc: String? = null): NotificationCompat.Builder {
        createNotificationChannel()
        return NotificationCompat.Builder(ctx, CHANNEL_ID).apply {
            setContentTitle(title)
            setContentText(desc)
            setSmallIcon(icon)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }
    }
    fun createNotification(title: String, icon: Int, desc: String? = null) =
        createNotification(title, IconCompat.createWithResource(ctx, icon), desc)
    fun close(notificationId: Int) = mgr.cancel(notificationId)
    @Suppress("unused")
    fun notify(id: Int, notification: Notification) = mgr.notify(id, notification)
}