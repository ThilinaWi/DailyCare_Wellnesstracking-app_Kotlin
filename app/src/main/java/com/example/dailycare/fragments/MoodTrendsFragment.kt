package com.example.dailycare.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dailycare.databinding.FragmentMoodTrendsBinding
import com.example.dailycare.models.MoodEntry
import com.example.dailycare.utils.PreferencesManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MoodTrendsFragment : Fragment() {

    private var _binding: FragmentMoodTrendsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var moodChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodTrendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager.getInstance(requireContext())
        moodChart = binding.moodChart
        
        setupChart()
        loadMoodData()
    }

    private fun setupChart() {
        moodChart.apply {
            // Chart styling
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)
            
            // X-axis styling
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.parseColor("#424242")
                textSize = 10f
            }
            
            // Y-axis styling
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#E0E0E0")
                textColor = Color.parseColor("#424242")
                textSize = 10f
                axisMinimum = 0f
                axisMaximum = 6f
                granularity = 1f
                // Custom labels for mood values
                setLabelCount(6, true)
            }
            
            axisRight.isEnabled = false
            legend.isEnabled = false
        }
    }

    private fun loadMoodData() {
        val moodEntries = preferencesManager.getMoodEntries()
        
        if (moodEntries.isEmpty()) {
            showEmptyState()
            return
        }
        
        updateStatistics(moodEntries)
        setupMoodChart(moodEntries)
        
        // Hide empty state
        binding.emptyStateLayout.visibility = View.GONE
    }

    private fun showEmptyState() {
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.tvTotalEntries.text = "0"
        binding.tvAverageMood.text = "0.0"
        binding.tvMostFrequentMood.text = "😐"
        moodChart.clear()
    }

    private fun updateStatistics(moodEntries: List<MoodEntry>) {
        // Total entries
        binding.tvTotalEntries.text = moodEntries.size.toString()
        
        // Average mood
        val averageMood = moodEntries.map { it.moodValue }.average()
        binding.tvAverageMood.text = String.format("%.1f", averageMood)
        
        // Most frequent mood
        val moodFrequency = HashMap<String, Int>()
        moodEntries.forEach { entry ->
            moodFrequency[entry.emoji] = moodFrequency.getOrDefault(entry.emoji, 0) + 1
        }
        val mostFrequentMood = moodFrequency.maxByOrNull { it.value }?.key ?: "😐"
        binding.tvMostFrequentMood.text = mostFrequentMood
    }

    private fun setupMoodChart(moodEntries: List<MoodEntry>) {
        // Get last 7 days of data
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val last7Days = mutableListOf<String>()
        val dayLabels = mutableListOf<String>()
        
        // Generate last 7 days
        for (i in 6 downTo 0) {
            calendar.add(Calendar.DAY_OF_MONTH, if (i == 6) -6 else 1)
            last7Days.add(dateFormat.format(calendar.time))
            dayLabels.add(SimpleDateFormat("MMM dd", Locale.getDefault()).format(calendar.time))
        }
        
        // Group mood entries by date and calculate average mood per day
        val moodByDate = HashMap<String, MutableList<Int>>()
        moodEntries.forEach { entry ->
            val entryDate = entry.date
            if (last7Days.contains(entryDate)) {
                if (!moodByDate.containsKey(entryDate)) {
                    moodByDate[entryDate] = mutableListOf()
                }
                moodByDate[entryDate]?.add(entry.moodValue)
            }
        }
        
        // Create chart entries
        val entries = mutableListOf<Entry>()
        last7Days.forEachIndexed { index, date ->
            val moodsForDay = moodByDate[date]
            val averageMood = if (moodsForDay != null && moodsForDay.isNotEmpty()) {
                moodsForDay.average().toFloat()
            } else {
                0f // No mood entry for this day
            }
            entries.add(Entry(index.toFloat(), averageMood))
        }
        
        // Create dataset
        val dataSet = LineDataSet(entries, "Mood Trend").apply {
            color = Color.parseColor("#2196F3")
            setCircleColor(Color.parseColor("#1976D2"))
            lineWidth = 3f
            circleRadius = 6f
            setDrawCircleHole(false)
            valueTextSize = 10f
            valueTextColor = Color.parseColor("#424242")
            setDrawFilled(true)
            fillColor = Color.parseColor("#E3F2FD")
            fillAlpha = 100
            mode = LineDataSet.Mode.CUBIC_BEZIER
            
            // Custom value formatter to show mood emojis
            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when {
                        value == 0f -> ""
                        value <= 1f -> "😢"
                        value <= 2f -> "😔"
                        value <= 3f -> "😐"
                        value <= 4f -> "😊"
                        else -> "😄"
                    }
                }
            }
        }
        
        // Set data to chart
        val lineData = LineData(dataSet)
        moodChart.data = lineData
        
        // Set custom X-axis labels
        moodChart.xAxis.valueFormatter = IndexAxisValueFormatter(dayLabels)
        
        // Animate chart
        moodChart.animateX(1000)
        moodChart.invalidate()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment becomes visible
        loadMoodData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}