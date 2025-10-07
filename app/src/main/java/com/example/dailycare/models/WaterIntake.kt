package com.example.dailycare.models

data class WaterIntake(
    val id: String,
    val amount: Int, // Amount in ml
    val timestamp: Long,
    val date: String
)