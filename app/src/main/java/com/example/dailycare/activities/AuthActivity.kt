package com.example.dailycare.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailycare.databinding.ActivityAuthBinding
import com.example.dailycare.utils.PreferencesManager

class AuthActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAuthBinding
    private lateinit var preferencesManager: PreferencesManager
    private var isSignUpMode = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferencesManager = PreferencesManager.getInstance(this)
        
        setupUI()
        setupClickListeners()
    }
    
    private fun setupUI() {
        updateUI()
    }
    
    private fun setupClickListeners() {
        binding.btnPrimary.setOnClickListener {
            if (isSignUpMode) {
                signUp()
            } else {
                signIn()
            }
        }
        
        binding.btnToggle.setOnClickListener {
            isSignUpMode = !isSignUpMode
            updateUI()
        }
    }
    
    private fun updateUI() {
        if (isSignUpMode) {
            binding.tvTitle.text = "Create Account"
            binding.btnPrimary.text = "Sign Up"
            binding.tvToggleText.text = "Already have an account?"
            binding.btnToggle.text = "Sign In"
        } else {
            binding.tvTitle.text = "Welcome Back"
            binding.btnPrimary.text = "Sign In"
            binding.tvToggleText.text = "Don't have an account?"
            binding.btnToggle.text = "Sign Up"
        }
    }
    
    private fun signUp() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password.length < 4) {
            Toast.makeText(this, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show()
            return
        }
        
        val success = preferencesManager.saveUser(username, password)
        if (success) {
            preferencesManager.setCurrentUsername(username)
            preferencesManager.setUserLoggedIn(true)
            preferencesManager.setOnboardingCompleted(true)
            
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
            goToDashboard()
        } else {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun signIn() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Check if user exists first
        if (!preferencesManager.userExists(username)) {
            Toast.makeText(this, "User not found. Please sign up first.", Toast.LENGTH_SHORT).show()
            return
        }
        
        val isValid = preferencesManager.validateUser(username, password)
        if (isValid) {
            preferencesManager.setCurrentUsername(username)
            preferencesManager.setUserLoggedIn(true)
            preferencesManager.setOnboardingCompleted(true)
            
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
            goToDashboard()
        } else {
            Toast.makeText(this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}