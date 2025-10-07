package com.example.dailycare.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailycare.adapters.HabitsAdapter
import com.example.dailycare.databinding.FragmentHabitsBinding
import com.example.dailycare.dialogs.AddHabitDialog
import com.example.dailycare.models.Habit
import com.example.dailycare.utils.PreferencesManager
import java.util.*

class HabitsFragment : Fragment(), HabitsAdapter.OnHabitClickListener {
    
    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var habitsAdapter: HabitsAdapter
    private val habitsList = mutableListOf<Habit>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager.getInstance(requireContext())
        setupRecyclerView()
        setupClickListeners()
        loadHabits()
    }
    
    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter(habitsList, this)
        binding.recyclerViewHabits.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = habitsAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
    }
    
    private fun loadHabits() {
        val habits = preferencesManager.getHabits()
        habitsList.clear()
        habitsList.addAll(habits)
        habitsAdapter.notifyDataSetChanged()
        
        // Show/hide empty state
        if (habitsList.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerViewHabits.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recyclerViewHabits.visibility = View.VISIBLE
        }
    }
    
    private fun showAddHabitDialog() {
        val dialog = AddHabitDialog { habitName ->
            addNewHabit(habitName)
        }
        dialog.show(parentFragmentManager, "AddHabitDialog")
    }
    
    private fun addNewHabit(habitName: String) {
        val newHabit = Habit(
            id = UUID.randomUUID().toString(),
            name = habitName,
            isCompleted = false,
            createdDate = preferencesManager.getCurrentDateString(),
            completedDate = null
        )
        
        habitsList.add(newHabit)
        saveHabits()
        habitsAdapter.notifyItemInserted(habitsList.size - 1)
        
        // Update UI state
        binding.emptyStateLayout.visibility = View.GONE
        binding.recyclerViewHabits.visibility = View.VISIBLE
        
        Toast.makeText(context, "Habit added successfully!", Toast.LENGTH_SHORT).show()
    }
    
    override fun onHabitClick(habit: Habit, position: Int) {
        // Toggle completion status
        val updatedHabit = habit.copy(
            isCompleted = !habit.isCompleted,
            completedDate = if (!habit.isCompleted) preferencesManager.getCurrentDateString() else null
        )
        
        habitsList[position] = updatedHabit
        saveHabits()
        habitsAdapter.notifyItemChanged(position)
        
        val message = if (updatedHabit.isCompleted) {
            "Great job! Habit completed! 🎉"
        } else {
            "Habit marked as incomplete"
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    override fun onHabitLongClick(habit: Habit, position: Int) {
        // Show delete confirmation
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete '${habit.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                deleteHabit(position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun deleteHabit(position: Int) {
        habitsList.removeAt(position)
        saveHabits()
        habitsAdapter.notifyItemRemoved(position)
        
        // Update UI state if list is empty
        if (habitsList.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerViewHabits.visibility = View.GONE
        }
        
        Toast.makeText(context, "Habit deleted", Toast.LENGTH_SHORT).show()
    }
    
    private fun saveHabits() {
        preferencesManager.saveHabits(habitsList)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}