package com.example.dailycare.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.dailycare.services.HydrationReminderReceiver

class HydrationReminderManager(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val preferencesManager = PreferencesManager.getInstance(context)
    
    companion object {
        private const val REMINDER_REQUEST_CODE = 1001
    }
    
    fun scheduleReminder() {
        if (!preferencesManager.isHydrationReminderEnabled()) {
            return
        }
        
        val intervalMinutes = preferencesManager.getHydrationInterval()
        val intervalMillis = intervalMinutes * 60 * 1000L
        
        val intent = Intent(context, HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Cancel any existing alarms first
        cancelReminder()
        
        val triggerTime = System.currentTimeMillis() + intervalMillis
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Handle permission issues gracefully
            e.printStackTrace()
        }
    }
    
    fun cancelReminder() {
        val intent = Intent(context, HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }
    
    fun updateReminderSchedule() {
        if (preferencesManager.isHydrationReminderEnabled()) {
            scheduleReminder()
        } else {
            cancelReminder()
        }
    }
}