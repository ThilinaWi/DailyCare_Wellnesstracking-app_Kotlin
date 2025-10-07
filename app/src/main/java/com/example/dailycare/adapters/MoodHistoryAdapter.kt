package com.example.dailycare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailycare.databinding.ItemMoodHistoryBinding
import com.example.dailycare.models.MoodEntry
import java.text.SimpleDateFormat
import java.util.*

class MoodHistoryAdapter(
    private val moodEntries: MutableList<MoodEntry>,
    private val onDeleteClick: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodHistoryAdapter.MoodHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodHistoryViewHolder {
        val binding = ItemMoodHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodHistoryViewHolder, position: Int) {
        holder.bind(moodEntries[position], onDeleteClick)
    }

    override fun getItemCount(): Int = moodEntries.size

    class MoodHistoryViewHolder(
        private val binding: ItemMoodHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(moodEntry: MoodEntry, onDeleteClick: (MoodEntry) -> Unit) {
            binding.apply {
                tvMoodEmoji.text = moodEntry.emoji
                tvMoodDate.text = formatDateWithTime(moodEntry.date, moodEntry.timestamp)
                tvMoodDescription.text = getMoodDescription(moodEntry.moodValue)
                
                // Show user's custom note if available
                if (moodEntry.description.isNotBlank()) {
                    tvMoodNote.text = "\"${moodEntry.description}\""
                    tvMoodNote.visibility = View.VISIBLE
                } else {
                    tvMoodNote.visibility = View.GONE
                }
                
                // Set up delete button click listener
                btnDeleteMood.setOnClickListener {
                    onDeleteClick(moodEntry)
                }
            }
        }

        private fun formatDateWithTime(dateString: String, timestamp: Long): String {
            return try {
                val date = Date(timestamp)
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                
                if (dateString == today) {
                    // For today's entries, show time only
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    "Today ${timeFormat.format(date)}"
                } else {
                    // For other days, show date and time
                    val dateTimeFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                    dateTimeFormat.format(date)
                }
            } catch (e: Exception) {
                dateString
            }
        }

        private fun getMoodDescription(value: Int): String {
            return when (value) {
                1 -> "Very Sad"
                2 -> "Sad"
                3 -> "Neutral"
                4 -> "Happy"
                5 -> "Very Happy"
                else -> "Unknown"
            }
        }
    }
}