package com.example.realmtesting2.Notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.realmtesting2.Activity.EditNotesActivity
import java.util.*

class AlarmUtils(base: Context) : ContextWrapper(base) {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun startAlarm(calendar: Calendar, notifContent: String, id: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val contentIntent = Intent(this, EditNotesActivity::class.java)
            contentIntent.putExtra("id",id)
        val contentPendingIntent = PendingIntent.getActivity(this,id,contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val intent = Intent(this, AlarmReceiver::class.java)
            intent.putExtra("content",notifContent)
            intent.putExtra("pendingContent",contentPendingIntent)
        val pendingIntent = PendingIntent.getBroadcast(this,id,intent,0)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pendingIntent)
    }

    fun cancelAlarm(id:Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,
            AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this,id,intent,0)

        alarmManager.cancel(pendingIntent)
        Toast.makeText(this,"Reminder Cancelled", Toast.LENGTH_SHORT).show()
    }

}