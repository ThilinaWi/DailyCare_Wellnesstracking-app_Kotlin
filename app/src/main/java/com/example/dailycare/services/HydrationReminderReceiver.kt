package com.example.dailycare.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dailycare.R
import com.example.dailycare.activities.DashboardActivity

class HydrationReminderReceiver : BroadcastReceiver() {
    
    companion object {
        const val CHANNEL_ID = "hydration_reminders"
        const val NOTIFICATION_ID = 1001
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val preferencesManager = com.example.dailycare.utils.PreferencesManager.getInstance(context)
            
            // Only show notification if reminders are enabled
            if (preferencesManager.isHydrationReminderEnabled()) {
                createNotificationChannel(context)
                showHydrationNotification(context)
                
                // Schedule the next reminder
                val reminderManager = com.example.dailycare.utils.HydrationReminderManager(context)
                reminderManager.scheduleReminder()
            }
        } catch (e: Exception) {
            // Log the error but don't crash
            e.printStackTrace()
        }
    }
    
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Hydration Reminders"
            val descriptionText = "Notifications to remind you to drink water"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun showHydrationNotification(context: Context) {
        val intent = Intent(context, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "hydration")
        }
        
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notificationMessages = listOf(
            "💧 Time to hydrate! Your body needs water.",
            "🥤 Don't forget to drink water!",
            "💦 Stay hydrated and healthy!",
            "🌊 Your daily hydration reminder is here!",
            "💧 A glass of water keeps you energized!"
        )
        
        val randomMessage = notificationMessages.random()
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle("Hydration Reminder")
            .setContentText(randomMessage)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        try {
            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } catch (e: SecurityException) {
            // Handle permission denial gracefully
            e.printStackTrace()
        }
    }
}