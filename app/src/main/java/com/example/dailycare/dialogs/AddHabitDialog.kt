package com.example.dailycare.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.dailycare.databinding.DialogAddHabitBinding

class AddHabitDialog(
    private val onHabitAdded: (String) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddHabitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddHabitBinding.inflate(layoutInflater)

        return AlertDialog.Builder(requireContext())
            .setTitle("Add New Habit")
            .setView(binding.root)
            .setPositiveButton("Add") { _, _ ->
                val habitName = binding.etHabitName.text.toString().trim()
                if (habitName.isNotEmpty()) {
                    onHabitAdded(habitName)
                } else {
                    Toast.makeText(context, "Please enter a habit name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}