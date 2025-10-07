package com.example.dailycare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailycare.databinding.ItemWaterHistoryBinding
import com.example.dailycare.models.WaterIntake
import java.text.SimpleDateFormat
import java.util.*

class WaterHistoryAdapter(
    private val waterIntakes: MutableList<WaterIntake>,
    private val onDeleteClick: (WaterIntake) -> Unit
) : RecyclerView.Adapter<WaterHistoryAdapter.WaterHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterHistoryViewHolder {
        val binding = ItemWaterHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WaterHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WaterHistoryViewHolder, position: Int) {
        holder.bind(waterIntakes[position], onDeleteClick)
    }

    override fun getItemCount(): Int = waterIntakes.size

    class WaterHistoryViewHolder(
        private val binding: ItemWaterHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(waterIntake: WaterIntake, onDeleteClick: (WaterIntake) -> Unit) {
            binding.apply {
                tvWaterAmount.text = "${waterIntake.amount}ml"
                tvWaterTime.text = formatTime(waterIntake.timestamp)
                
                // Set up delete button click listener
                btnDeleteWater.setOnClickListener {
                    onDeleteClick(waterIntake)
                }
            }
        }

        private fun formatTime(timestamp: Long): String {
            return try {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                timeFormat.format(Date(timestamp))
            } catch (e: Exception) {
                "Unknown"
            }
        }
    }
}