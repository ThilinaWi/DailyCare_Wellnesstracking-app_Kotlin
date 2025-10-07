package com.example.dailycare.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.dailycare.R

class EditHabitDialog(
    private val currentHabitName: String,
    private val onHabitEdited: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val editText = EditText(requireContext()).apply {
            setText(currentHabitName)
            hint = getString(R.string.habit_name_hint)
            setPadding(50, 30, 50, 30)
            setSelection(currentHabitName.length) // Place cursor at end
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.edit_habit))
            .setView(editText)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val habitName = editText.text.toString().trim()
                if (habitName.isNotEmpty()) {
                    onHabitEdited(habitName)
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
    }
}