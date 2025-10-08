package com.example.dailycare.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dailycare.databinding.FragmentHydrationBinding
import com.example.dailycare.models.WaterIntake
import com.example.dailycare.utils.PreferencesManager
import com.example.dailycare.utils.HydrationReminderManager
import java.util.*

class HydrationFragment : Fragment() {
    
    private var _binding: FragmentHydrationBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var hydrationReminderManager: HydrationReminderManager
    
    // Permission launcher for notification permission
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Notification permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Notification permission denied. Reminders may not work.", Toast.LENGTH_LONG).show()
            // Optionally disable the reminder switch
            binding.switchHydrationReminder?.isChecked = false
            preferencesManager.setHydrationReminderEnabled(false)
        }
    }
    
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
        hydrationReminderManager = HydrationReminderManager(requireContext())
        setupWaterButtons()
        setupReminderSettings()
        loadTodaysWaterIntake()
    }
    
    private fun setupWaterButtons() {
        try {
            binding.btnAdd250ml?.setOnClickListener { addWaterSafely(250) }
            binding.btnAdd500ml?.setOnClickListener { addWaterSafely(500) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    
    private fun setupReminderSettings() {
        try {
            // Setup reminder toggle
            binding.switchHydrationReminder?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Check notification permission for Android 13+
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        when {
                            ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                // Permission already granted
                                enableReminders()
                            }
                            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                                // Show rationale and request permission
                                Toast.makeText(
                                    requireContext(),
                                    "Notification permission is needed for hydration reminders",
                                    Toast.LENGTH_LONG
                                ).show()
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            else -> {
                                // Request permission directly
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    } else {
                        // Pre-Android 13, no runtime permission needed
                        enableReminders()
                    }
                } else {
                    // Disable reminders
                    preferencesManager.setHydrationReminderEnabled(false)
                    hydrationReminderManager.updateReminderSchedule()
                    updateReminderStatus()
                }
            }
            
            // Setup interval SeekBar (1 minute to 3 hours = 1-180 minutes)
            binding.seekBarInterval?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        val minutes = progress + 1 // 0-179 becomes 1-180
                        preferencesManager.setHydrationInterval(minutes)
                        updateIntervalDisplay(minutes)
                        if (preferencesManager.isHydrationReminderEnabled()) {
                            hydrationReminderManager.updateReminderSchedule()
                        }
                        updateReminderStatus()
                    }
                }
                
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            
            // Setup test notification button
            binding.btnTestNotification?.setOnClickListener {
                showTestNotification()
            }
            
            // Load current settings
            loadReminderSettings()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadReminderSettings() {
        try {
            val isEnabled = preferencesManager.isHydrationReminderEnabled()
            val intervalMinutes = preferencesManager.getHydrationInterval()
            
            binding.switchHydrationReminder?.isChecked = isEnabled
            binding.seekBarInterval?.progress = intervalMinutes - 1 // Convert back to 0-179
            
            updateIntervalDisplay(intervalMinutes)
            updateReminderStatus()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun updateIntervalDisplay(minutes: Int) {
        try {
            val displayText = when {
                minutes < 60 -> "$minutes minutes"
                minutes == 60 -> "1 hour"
                minutes % 60 == 0 -> "${minutes / 60} hours"
                else -> "${minutes / 60}h ${minutes % 60}m"
            }
            binding.tvCurrentInterval?.text = displayText
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun updateReminderStatus() {
        try {
            val isEnabled = preferencesManager.isHydrationReminderEnabled()
            val intervalMinutes = preferencesManager.getHydrationInterval()
            val intervalText = when {
                intervalMinutes < 60 -> "Every $intervalMinutes minutes"
                intervalMinutes == 60 -> "Every hour"
                intervalMinutes % 60 == 0 -> "Every ${intervalMinutes / 60} hours"
                else -> "Every ${intervalMinutes / 60}h ${intervalMinutes % 60}m"
            }
            
            val statusText = if (isEnabled) {
                "Active - $intervalText"
            } else {
                "Inactive - $intervalText"
            }
            
            binding.tvReminderStatus?.text = statusText
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun showTestNotification() {
        try {
            // Trigger a test notification immediately
            val context = context ?: return
            val intent = android.content.Intent(context, com.example.dailycare.services.HydrationReminderReceiver::class.java)
            context.sendBroadcast(intent)
            
            // Show confirmation
            android.app.AlertDialog.Builder(context)
                .setTitle("Test Notification Sent")
                .setMessage("Check your notification panel for the hydration reminder.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
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
            } catch (uiException: Exception) {
                uiException.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    
    private fun enableReminders() {
        preferencesManager.setHydrationReminderEnabled(true)
        hydrationReminderManager.updateReminderSchedule()
        updateReminderStatus()
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

    

    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}