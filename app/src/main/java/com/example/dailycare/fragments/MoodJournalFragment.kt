package com.example.dailycare.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailycare.adapters.MoodHistoryAdapter
import com.example.dailycare.databinding.FragmentMoodJournalBinding
import com.example.dailycare.models.MoodEntry
import com.example.dailycare.utils.PreferencesManager
import java.util.*

class MoodJournalFragment : Fragment() {
    
    private var _binding: FragmentMoodJournalBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var moodHistoryAdapter: MoodHistoryAdapter
    private val moodHistoryList = mutableListOf<MoodEntry>()
    
    private val moodEmojis = listOf(
        Pair("😢", 1), // Very Sad
        Pair("😞", 2), // Sad
        Pair("😐", 3), // Neutral
        Pair("😊", 4), // Happy
        Pair("😄", 5)  // Very Happy
    )
    
    private var selectedMoodEmoji = ""
    private var selectedMoodValue = 0
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodJournalBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager.getInstance(requireContext())
        setupMoodSelector()
        setupMoodDescriptionCard()
        setupMoodHistory()
        loadTodaysMood()
        loadMoodHistory()
    }
    
    private fun setupMoodDescriptionCard() {
        try {
            // Initially hide the description card
            binding.cardMoodDescription?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupMoodSelector() {
        try {
            // Set up mood emoji buttons safely
            binding.btnMood1?.text = moodEmojis[0].first
            binding.btnMood2?.text = moodEmojis[1].first
            binding.btnMood3?.text = moodEmojis[2].first
            binding.btnMood4?.text = moodEmojis[3].first
            binding.btnMood5?.text = moodEmojis[4].first
            
            // Set click listeners to show description input
            binding.btnMood1?.setOnClickListener { selectMoodSafely(moodEmojis[0].first, moodEmojis[0].second) }
            binding.btnMood2?.setOnClickListener { selectMoodSafely(moodEmojis[1].first, moodEmojis[1].second) }
            binding.btnMood3?.setOnClickListener { selectMoodSafely(moodEmojis[2].first, moodEmojis[2].second) }
            binding.btnMood4?.setOnClickListener { selectMoodSafely(moodEmojis[3].first, moodEmojis[3].second) }
            binding.btnMood5?.setOnClickListener { selectMoodSafely(moodEmojis[4].first, moodEmojis[4].second) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun selectMoodSafely(emoji: String, value: Int) {
        try {
            selectedMoodEmoji = emoji
            selectedMoodValue = value
            showMoodDescriptionInput()
        } catch (e: Exception) {
            // If showing description fails, save mood directly
            saveMoodSafely(emoji, value, "")
        }
    }
    
    private fun showMoodDescriptionInput() {
        try {
            binding.cardMoodDescription?.visibility = View.VISIBLE
            binding.etMoodDescription?.text?.clear()
            binding.etMoodDescription?.requestFocus()
            
            // Setup save and cancel buttons
            binding.btnSaveMood?.setOnClickListener {
                val description = binding.etMoodDescription?.text?.toString()?.trim() ?: ""
                saveMoodSafely(selectedMoodEmoji, selectedMoodValue, description)
                hideMoodDescriptionInput()
            }
            
            binding.btnCancelMood?.setOnClickListener {
                hideMoodDescriptionInput()
            }
        } catch (e: Exception) {
            // If UI fails, save mood without description
            saveMoodSafely(selectedMoodEmoji, selectedMoodValue, "")
        }
    }
    
    private fun hideMoodDescriptionInput() {
        try {
            binding.cardMoodDescription?.visibility = View.GONE
            binding.etMoodDescription?.text?.clear()
            selectedMoodEmoji = ""
            selectedMoodValue = 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun saveMoodSafely(emoji: String, value: Int, description: String = "") {
        try {
            val currentEntries = preferencesManager.getMoodEntries().toMutableList()
            val today = preferencesManager.getCurrentDateString()
            
            // Add new mood entry (allow multiple entries per day)
            val newMoodEntry = MoodEntry(
                id = UUID.randomUUID().toString(),
                emoji = emoji,
                moodValue = value,
                date = today,
                timestamp = preferencesManager.getCurrentTimestamp(),
                description = description
            )
            
            currentEntries.add(newMoodEntry)
            preferencesManager.saveMoodEntries(currentEntries)
            
            // Try to update UI, but don't crash if it fails
            try {
                loadTodaysMood()
                loadMoodHistory()
            } catch (uiException: Exception) {
                // UI update failed, but mood is saved
                uiException.printStackTrace()
            }
        } catch (e: Exception) {
            // Even if something fails, don't crash
            e.printStackTrace()
        }
    }
    

    
    private fun setupMoodHistory() {
        moodHistoryAdapter = MoodHistoryAdapter(moodHistoryList) { moodEntry ->
            deleteMoodEntry(moodEntry)
        }
        binding.recyclerViewMoodHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moodHistoryAdapter
        }
    }
    
    private fun loadTodaysMood() {
        try {
            val today = preferencesManager.getCurrentDateString()
            val todaysMoods = preferencesManager.getMoodEntries()
                .filter { it.date == today }
                .sortedByDescending { it.timestamp }
            
            if (todaysMoods.isNotEmpty()) {
                val latestMood = todaysMoods.first()
                val totalTodayMoods = todaysMoods.size
                
                binding.tvTodayMood.text = latestMood.emoji
                binding.tvMoodStatus.text = if (totalTodayMoods == 1) {
                    "Today's mood logged!"
                } else {
                    "$totalTodayMoods moods logged today"
                }
                context?.let { ctx ->
                    binding.tvMoodStatus.setTextColor(
                        ctx.getColor(android.R.color.holo_green_dark)
                    )
                }
            } else {
                binding.tvTodayMood.text = "❓"
                binding.tvMoodStatus.text = "How are you feeling today?"
                context?.let { ctx ->
                    binding.tvMoodStatus.setTextColor(
                        ctx.getColor(com.example.dailycare.R.color.dark_gray)
                    )
                }
            }
        } catch (e: Exception) {
            // Handle any errors gracefully
            e.printStackTrace()
        }
    }
    
    private fun loadMoodHistory() {
        val moodEntries = preferencesManager.getMoodEntries()
        moodHistoryList.clear()
        moodHistoryList.addAll(moodEntries.sortedByDescending { it.timestamp })
        moodHistoryAdapter.notifyDataSetChanged()
        
        // Update UI visibility
        if (moodHistoryList.isEmpty()) {
            binding.tvNoMoodHistory.visibility = View.VISIBLE
            binding.recyclerViewMoodHistory.visibility = View.GONE
        } else {
            binding.tvNoMoodHistory.visibility = View.GONE
            binding.recyclerViewMoodHistory.visibility = View.VISIBLE
        }
    }
    
    private fun saveMood(emoji: String, value: Int, description: String = "") {
        try {
            val currentEntries = preferencesManager.getMoodEntries().toMutableList()
            val today = preferencesManager.getCurrentDateString()
            
            // Add new mood entry (allow multiple entries per day)
            val newMoodEntry = MoodEntry(
                id = UUID.randomUUID().toString(),
                emoji = emoji,
                moodValue = value,
                date = today,
                timestamp = preferencesManager.getCurrentTimestamp(),
                description = description
            )
            
            currentEntries.add(newMoodEntry)
            preferencesManager.saveMoodEntries(currentEntries)
            
            // Update UI safely
            loadTodaysMood()
            loadMoodHistory()
            
            // Mood saved successfully - no toast to avoid crashes
        } catch (e: Exception) {
            // Handle any errors gracefully
            e.printStackTrace()
        }
    }
    
    private fun deleteTodaysMood() {
        val currentEntries = preferencesManager.getMoodEntries().toMutableList()
        val today = preferencesManager.getCurrentDateString()
        
        // Find and remove the latest mood entry for today
        val todaysMoods = currentEntries.filter { it.date == today }.sortedByDescending { it.timestamp }
        if (todaysMoods.isNotEmpty()) {
            val latestMood = todaysMoods.first()
            currentEntries.removeIf { it.id == latestMood.id }
            preferencesManager.saveMoodEntries(currentEntries)
            
            // Update UI
            loadTodaysMood()
            loadMoodHistory()
            
            // Mood deleted successfully - no toast to avoid crashes
        }
    }
    
    private fun deleteMoodEntry(moodEntry: MoodEntry) {
        try {
            val currentEntries = preferencesManager.getMoodEntries().toMutableList()
            
            // Remove the specific mood entry
            currentEntries.removeIf { it.id == moodEntry.id }
            preferencesManager.saveMoodEntries(currentEntries)
            
            // Update UI safely
            try {
                loadTodaysMood()
                loadMoodHistory()
            } catch (uiException: Exception) {
                // UI update failed, but mood is deleted
                uiException.printStackTrace()
            }
        } catch (e: Exception) {
            // Handle any errors gracefully
            e.printStackTrace()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}