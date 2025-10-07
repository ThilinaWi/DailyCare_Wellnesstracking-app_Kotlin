package com.example.dailycare.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailycare.adapters.WaterHistoryAdapter
import com.example.dailycare.databinding.FragmentHydrationBinding
import com.example.dailycare.models.WaterIntake
import com.example.dailycare.utils.PreferencesManager
import java.util.*

class HydrationFragment : Fragment() {
    
    private var _binding: FragmentHydrationBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var waterHistoryAdapter: WaterHistoryAdapter
    private val waterHistoryList = mutableListOf<WaterIntake>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHydrationBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager.getInstance(requireContext())
        setupWaterButtons()
        setupShareButton()
        setupWaterHistory()
        loadTodaysWaterIntake()
        loadWaterHistory()
    }
    
    private fun setupWaterButtons() {
        try {
            binding.btnAdd250ml?.setOnClickListener { addWaterSafely(250) }
            binding.btnAdd500ml?.setOnClickListener { addWaterSafely(500) }
            binding.btnAdd1000ml?.setOnClickListener { addWaterSafely(1000) }
            binding.btnCustomAmount?.setOnClickListener { showCustomAmountDialog() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupShareButton() {
        try {
            binding.btnShareWaterData?.setOnClickListener {
                shareWaterData()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupWaterHistory() {
        waterHistoryAdapter = WaterHistoryAdapter(waterHistoryList) { waterIntake ->
            deleteWaterEntry(waterIntake)
        }
        binding.recyclerViewWaterHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = waterHistoryAdapter
        }
    }
    
    private fun addWaterSafely(amount: Int) {
        try {
            val currentIntakes = preferencesManager.getWaterIntake().toMutableList()
            val today = preferencesManager.getCurrentDateString()
            
            val newWaterIntake = WaterIntake(
                id = UUID.randomUUID().toString(),
                amount = amount,
                timestamp = preferencesManager.getCurrentTimestamp(),
                date = today
            )
            
            currentIntakes.add(newWaterIntake)
            preferencesManager.saveWaterIntake(currentIntakes)
            
            // Update UI safely
            try {
                loadTodaysWaterIntake()
                loadWaterHistory()
            } catch (uiException: Exception) {
                uiException.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun showCustomAmountDialog() {
        try {
            val context = context ?: return
            val editText = EditText(context).apply {
                hint = "Enter amount in ml"
                inputType = android.text.InputType.TYPE_CLASS_NUMBER
            }
            
            AlertDialog.Builder(context)
                .setTitle("Add Custom Water Amount")
                .setMessage("Enter the amount of water you drank:")
                .setView(editText)
                .setPositiveButton("Add") { _, _ ->
                    try {
                        val amount = editText.text.toString().toIntOrNull()
                        if (amount != null && amount > 0 && amount <= 5000) {
                            addWaterSafely(amount)
                        } else {
                            showInvalidAmountMessage()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadTodaysWaterIntake() {
        try {
            val today = preferencesManager.getCurrentDateString()
            val todaysIntakes = preferencesManager.getWaterIntake()
                .filter { it.date == today }
            
            val totalIntake = todaysIntakes.sumOf { it.amount }
            val dailyGoal = preferencesManager.getDailyWaterGoal()
            val progressPercentage = ((totalIntake.toFloat() / dailyGoal) * 100).toInt().coerceAtMost(100)
            
            binding.tvCurrentIntake.text = totalIntake.toString()
            binding.tvDailyGoal.text = dailyGoal.toString()
            binding.progressBarWater.progress = progressPercentage
            binding.tvProgressPercentage.text = "$progressPercentage% of daily goal"
            
            // Update progress bar color based on achievement
            val progressColor = when {
                progressPercentage >= 100 -> android.R.color.holo_green_dark
                progressPercentage >= 50 -> com.example.dailycare.R.color.accent_cobalt_blue
                else -> com.example.dailycare.R.color.warning_orange
            }
            
            context?.let { ctx ->
                binding.progressBarWater.progressTintList = 
                    android.content.res.ColorStateList.valueOf(ctx.getColor(progressColor))
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadWaterHistory() {
        try {
            val today = preferencesManager.getCurrentDateString()
            val todaysIntakes = preferencesManager.getWaterIntake()
                .filter { it.date == today }
                .sortedByDescending { it.timestamp }
            
            waterHistoryList.clear()
            waterHistoryList.addAll(todaysIntakes)
            waterHistoryAdapter.notifyDataSetChanged()
            
            // Update UI visibility
            if (waterHistoryList.isEmpty()) {
                binding.tvNoWaterHistory.visibility = View.VISIBLE
                binding.recyclerViewWaterHistory.visibility = View.GONE
            } else {
                binding.tvNoWaterHistory.visibility = View.GONE
                binding.recyclerViewWaterHistory.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun deleteWaterEntry(waterIntake: WaterIntake) {
        try {
            val currentIntakes = preferencesManager.getWaterIntake().toMutableList()
            
            // Remove the specific water entry
            currentIntakes.removeIf { it.id == waterIntake.id }
            preferencesManager.saveWaterIntake(currentIntakes)
            
            // Update UI safely
            try {
                loadTodaysWaterIntake()
                loadWaterHistory()
            } catch (uiException: Exception) {
                uiException.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun shareWaterData() {
        try {
            val today = preferencesManager.getCurrentDateString()
            val todaysIntakes = preferencesManager.getWaterIntake()
                .filter { it.date == today }
                .sortedByDescending { it.timestamp }
            
            if (todaysIntakes.isEmpty()) {
                showNoWaterDataMessage()
                return
            }
            
            // Create CSV content
            val csvContent = generateWaterDataCSV(todaysIntakes)
            
            // Share the CSV content
            shareCSVContent(csvContent)
            
        } catch (e: Exception) {
            e.printStackTrace()
            showShareErrorMessage()
        }
    }
    
    private fun generateWaterDataCSV(waterIntakes: List<WaterIntake>): String {
        val csvBuilder = StringBuilder()
        
        // Add CSV header
        csvBuilder.append("Date,Time,Amount_ml\n")
        
        // Add water entries
        waterIntakes.forEach { intake ->
            try {
                val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date(intake.timestamp))
                val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date(intake.timestamp))
                
                csvBuilder.append("$date,$time,${intake.amount}\n")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        // Add summary
        val totalIntake = waterIntakes.sumOf { it.amount }
        val dailyGoal = preferencesManager.getDailyWaterGoal()
        csvBuilder.append("\nSummary\n")
        csvBuilder.append("Total Intake,${totalIntake}ml\n")
        csvBuilder.append("Daily Goal,${dailyGoal}ml\n")
        csvBuilder.append("Achievement,${((totalIntake.toFloat() / dailyGoal) * 100).toInt()}%\n")
        
        return csvBuilder.toString()
    }
    
    private fun shareCSVContent(csvContent: String) {
        try {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, csvContent)
                putExtra(Intent.EXTRA_SUBJECT, "My Water Intake Data")
            }
            
            val chooserIntent = Intent.createChooser(shareIntent, "Share Water Data")
            startActivity(chooserIntent)
            
        } catch (e: Exception) {
            e.printStackTrace()
            showShareErrorMessage()
        }
    }
    
    private fun showInvalidAmountMessage() {
        try {
            val context = context ?: return
            AlertDialog.Builder(context)
                .setTitle("Invalid Amount")
                .setMessage("Please enter a valid amount between 1ml and 5000ml.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun showNoWaterDataMessage() {
        try {
            val context = context ?: return
            AlertDialog.Builder(context)
                .setTitle("No Data to Share")
                .setMessage("You haven't logged any water intake today. Add some water first to share your data.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun showShareErrorMessage() {
        try {
            val context = context ?: return
            AlertDialog.Builder(context)
                .setTitle("Share Failed")
                .setMessage("There was an error sharing your water data. Please try again.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}