package com.example.dailycare.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.dailycare.utils.HydrationReminderManager
import com.example.dailycare.utils.PreferencesManager

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                
                try {
                    // Restart hydration reminders if they were enabled
                    val preferencesManager = PreferencesManager.getInstance(context)
                    if (preferencesManager.isHydrationReminderEnabled()) {
                        // Small delay to ensure system is ready
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            try {
                                val reminderManager = HydrationReminderManager(context)
                                reminderManager.scheduleReminder()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, 3000) // 3 second delay
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}