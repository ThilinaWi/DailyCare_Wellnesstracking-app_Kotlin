package com.example.dailycare.models

data class Habit(
    val id: String,
    val name: String,
    val isCompleted: Boolean = false,
    val createdDate: String,
    val completedDate: String? = null
)