package com.example.dailycare.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dailycare.R
import com.example.dailycare.databinding.ActivityDashboardBinding
import com.example.dailycare.fragments.HomeFragment
import com.example.dailycare.fragments.HabitsFragment
import com.example.dailycare.fragments.HydrationFragment
import com.example.dailycare.fragments.MoodJournalFragment
import com.example.dailycare.fragments.MoodTrendsFragment

class DashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
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