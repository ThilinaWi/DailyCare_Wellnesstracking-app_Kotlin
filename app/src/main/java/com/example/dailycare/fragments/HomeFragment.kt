package com.example.dailycare.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dailycare.activities.DashboardActivity
import com.example.dailycare.R
import com.example.dailycare.databinding.FragmentHomeBinding
import com.example.dailycare.utils.PreferencesManager
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesManager = PreferencesManager.getInstance(requireContext())
        
        setupWelcomeMessage()
        updateHabitsProgress()
        updateMoodStatus()
        setupQuickActions()
    }

    private fun setupWelcomeMessage() {
        val userName = preferencesManager.getCurrentUsername() ?: "User"
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        
        val greeting = when (currentHour) {
            in 5..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            in 18..21 -> "Good Evening"
            else -> "Good Night"
        }
        
        binding.tvWelcome.text = "$greeting, $userName!"
        
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        binding.tvDate.text = dateFormat.format(Date())
    }

    private fun updateHabitsProgress() {
        val today = preferencesManager.getCurrentDateString()
        val habits = preferencesManager.getHabits()
        val completedHabits = habits.filter { it.isCompleted && it.completedDate == today }
        
        val totalHabits = habits.size
        val completedCount = completedHabits.size
        
        binding.tvHabitsProgress.text = "$completedCount / $totalHabits Habits Completed"
        
        if (totalHabits > 0) {
            val percentage = (completedCount * 100) / totalHabits
            binding.progressHabits.progress = percentage
            binding.tvHabitsPercentage.text = "$percentage%"
            
            // Set color based on progress
            val colorRes = when {
                completedCount == totalHabits -> R.color.accent_electric_blue
                percentage >= 50 -> R.color.accent_cobalt_blue
                else -> R.color.secondary_navy_blue
            }
            binding.progressHabits.progressTintList = 
                context?.getColorStateList(colorRes)
        } else {
            binding.progressHabits.progress = 0
            binding.tvHabitsPercentage.text = "0%"
            binding.progressHabits.progressTintList = 
                context?.getColorStateList(R.color.primary_light_blue)
        }
    }

    private fun updateMoodStatus() {
        val today = preferencesManager.getCurrentDateString()
        val todayMoods = preferencesManager.getMoodEntries()
            .filter { it.date == today }
            .sortedByDescending { it.timestamp }
        
        if (todayMoods.isNotEmpty()) {
            val latestMood = todayMoods.first()
            val totalMoods = todayMoods.size
            
            val moodEmoji = when (latestMood.moodValue) {
                1 -> "😢"
                2 -> "😞"
                3 -> "😐"
                4 -> "😊"
                5 -> "😁"
                else -> "😐"
            }
            
            val moodText = when (latestMood.moodValue) {
                1 -> "Very Sad"
                2 -> "Sad"
                3 -> "Neutral"
                4 -> "Happy"
                5 -> "Very Happy"
                else -> "Neutral"
            }
            
            binding.tvMoodEmoji.text = moodEmoji
            binding.tvMoodStatus.text = if (totalMoods == 1) {
                "Today you're feeling $moodText"
            } else {
                "Latest: $moodText ($totalMoods moods today)"
            }
            binding.cardMoodStatus.alpha = 1.0f
        } else {
            binding.tvMoodEmoji.text = "❓"
            binding.tvMoodStatus.text = "How are you feeling today?"
            binding.cardMoodStatus.alpha = 0.7f
        }
    }

    private fun setupQuickActions() {
        // Quick habit add button
        binding.btnQuickHabit.setOnClickListener {
            // Navigate to Habits tab
            (activity as? DashboardActivity)?.navigateToTab(1)
        }
        
        // Quick mood log button
        binding.btnQuickMood.setOnClickListener {
            // Navigate to Mood tab
            (activity as? DashboardActivity)?.navigateToTab(2)
        }
        
        // Water reminder button
        binding.btnWaterReminder.setOnClickListener {
            // Navigate to Water tab
            (activity as? DashboardActivity)?.navigateToTab(3)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to home
        updateHabitsProgress()
        updateMoodStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}