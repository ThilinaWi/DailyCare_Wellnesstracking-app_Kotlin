package com.example.dailycare.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dailycare.R
import com.example.dailycare.databinding.ActivityDashboardBinding
import com.example.dailycare.fragments.HomeFragment
import com.example.dailycare.fragments.HabitsFragment
import com.example.dailycare.fragments.HydrationFragment
import com.example.dailycare.fragments.MoodJournalFragment
import com.example.dailycare.fragments.MoodTrendsFragment
import com.example.dailycare.utils.PreferencesManager

class DashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupBottomNavigation()
        
        // Handle navigation from notification
        val navigateTo = intent.getStringExtra("navigate_to")
        if (navigateTo == "hydration") {
            binding.bottomNavigation.selectedItemId = R.id.nav_water
            loadFragment(HydrationFragment())
        } else if (savedInstanceState == null) {
            // Load default fragment
            loadFragment(HomeFragment())
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.app_name)
            setDisplayShowTitleEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log Out") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        // Clear user session
        val preferencesManager = PreferencesManager.getInstance(this)
        preferencesManager.clearUserSession()
        
        // Navigate to AuthActivity
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_habit -> {
                    loadFragment(HabitsFragment())
                    true
                }
                R.id.nav_mood -> {
                    loadFragment(MoodJournalFragment())
                    true
                }
                R.id.nav_water -> {
                    loadFragment(HydrationFragment())
                    true
                }
                R.id.nav_trend -> {
                    loadFragment(MoodTrendsFragment())
                    true
                }
                else -> false
            }
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
    
    fun navigateToTab(tabIndex: Int) {
        val itemId = when (tabIndex) {
            0 -> R.id.nav_home
            1 -> R.id.nav_habit
            2 -> R.id.nav_mood
            3 -> R.id.nav_water
            4 -> R.id.nav_trend
            else -> R.id.nav_home
        }
        binding.bottomNavigation.selectedItemId = itemId
    }
}