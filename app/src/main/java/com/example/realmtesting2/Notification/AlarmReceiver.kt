package com.example.realmtesting2.Notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        lateinit var content:String
        lateinit var contentPendingIntent: PendingIntent

        content = intent.getStringExtra("content").toString()
        contentPendingIntent = intent.getParcelableExtra<PendingIntent>("pendingContent")!!

        val notification =
            NotificationUtils(context)
        val nb = notification.getChannelNotification(content,contentPendingIntent)
        notification.getManager()!!.notify(1,nb.build())
    }
}