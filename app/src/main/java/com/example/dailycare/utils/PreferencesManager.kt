package com.example.dailycare.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.dailycare.models.Habit
import com.example.dailycare.models.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class PreferencesManager private constructor(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "daily_care_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_USER_LOGGED_IN = "user_logged_in"
        private const val KEY_CURRENT_USERNAME = "current_username"
        private const val KEY_USERS = "users"
        private const val KEY_HABITS = "habits"
        private const val KEY_MOOD_ENTRIES = "mood_entries"
        private const val KEY_HYDRATION_REMINDER_ENABLED = "hydration_reminder_enabled"
        private const val KEY_HYDRATION_INTERVAL = "hydration_interval"
        
        @Volatile
        private var INSTANCE: PreferencesManager? = null
        
        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferencesManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    // Onboarding
    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }
    
    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
    
    // User Authentication
    fun setUserLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_USER_LOGGED_IN, loggedIn).apply()
    }
    
    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_USER_LOGGED_IN, false)
    }
    
    fun setCurrentUsername(username: String) {
        prefs.edit().putString(KEY_CURRENT_USERNAME, username).apply()
    }
    
    fun getCurrentUsername(): String? {
        return prefs.getString(KEY_CURRENT_USERNAME, null)
    }
    
    // User Management
    fun saveUser(username: String, password: String): Boolean {
        val users = getUsers().toMutableMap()
        if (users.containsKey(username)) {
            return false // User already exists
        }
        users[username] = password
        val json = gson.toJson(users)
        prefs.edit().putString(KEY_USERS, json).apply()
        return true
    }
    
    fun validateUser(username: String, password: String): Boolean {
        val users = getUsers()
        return users[username] == password
    }
    
    private fun getUsers(): Map<String, String> {
        val json = prefs.getString(KEY_USERS, null) ?: return emptyMap()
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(json, type) ?: emptyMap()
    }
    
    // Habits Management
    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString(KEY_HABITS, json).apply()
    }
    
    fun getHabits(): List<Habit> {
        val json = prefs.getString(KEY_HABITS, null) ?: return emptyList()
        val type = object : TypeToken<List<Habit>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    // Mood Entries Management
    fun saveMoodEntries(moodEntries: List<MoodEntry>) {
        val json = gson.toJson(moodEntries)
        prefs.edit().putString(KEY_MOOD_ENTRIES, json).apply()
    }
    
    fun getMoodEntries(): List<MoodEntry> {
        val json = prefs.getString(KEY_MOOD_ENTRIES, null) ?: return emptyList()
        val type = object : TypeToken<List<MoodEntry>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    fun getTodaysMoodEntry(): MoodEntry? {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return getMoodEntries().find { it.date == today }
    }
    
    // Hydration Reminders
    fun setHydrationReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HYDRATION_REMINDER_ENABLED, enabled).apply()
    }
    
    fun isHydrationReminderEnabled(): Boolean {
        return prefs.getBoolean(KEY_HYDRATION_REMINDER_ENABLED, false)
    }
    
    fun setHydrationInterval(intervalHours: Int) {
        prefs.edit().putInt(KEY_HYDRATION_INTERVAL, intervalHours).apply()
    }
    
    fun getHydrationInterval(): Int {
        return prefs.getInt(KEY_HYDRATION_INTERVAL, 2) // Default 2 hours
    }
    
    // Utility Methods
    fun getCurrentDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
}