package com.example.dailycare.models

data class MoodEntry(
    val id: String,
    val emoji: String,
    val moodValue: Int, // 1-5 scale for chart display
    val date: String,
    val timestamp: Long,
    val description: String = "" // Small description/note for the mood
)