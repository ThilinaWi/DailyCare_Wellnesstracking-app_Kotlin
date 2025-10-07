package com.example.dailycare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dailycare.activities.DashboardActivity
import com.example.dailycare.activities.OnboardingActivity
import com.example.dailycare.utils.PreferencesManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        preferencesManager = PreferencesManager.getInstance(this)
        
        // Check if user has completed onboarding and is logged in
        when {
            !preferencesManager.isOnboardingCompleted() -> {
                // Show onboarding
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            preferencesManager.isUserLoggedIn() -> {
                // Go to dashboard
                startActivity(Intent(this, DashboardActivity::class.java))
            }
            else -> {
                // Show onboarding (includes sign in/up)
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
        }
        
        finish()
    }
}