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
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    // Android 12+ - Check if we can schedule exact alarms
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent
                        )
                    } else {
                        // Fallback to inexact alarm
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent
                        )
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
                else -> {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            }
            
            // Schedule the next reminder immediately after this one
            scheduleRepeatingReminder(intervalMillis)
            
        } catch (e: SecurityException) {
            // Handle permission issues gracefully
            e.printStackTrace()
        } catch (e: Exception) {
            // Handle any other issues
            e.printStackTrace()
        }
    }
    
    private fun scheduleRepeatingReminder(intervalMillis: Long) {
        val intent = Intent(context, HydrationReminderReceiver::class.java)
        intent.putExtra("schedule_next", true)
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE + 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val nextTriggerTime = System.currentTimeMillis() + (intervalMillis * 2)
        
        try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            nextTriggerTime,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            nextTriggerTime,
                            pendingIntent
                        )
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextTriggerTime,
                        pendingIntent
                    )
                }
                else -> {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        nextTriggerTime,
                        pendingIntent
                    )
                }
            }
        } catch (e: Exception) {
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