package com.example.dailycare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailycare.databinding.ItemHabitBinding
import com.example.dailycare.models.Habit

class HabitsAdapter(
    private val habits: List<Habit>,
    private val listener: OnHabitClickListener
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

    interface OnHabitClickListener {
        fun onHabitClick(habit: Habit, position: Int)
        fun onHabitLongClick(habit: Habit, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position], position)
    }

    override fun getItemCount(): Int = habits.size

    inner class HabitViewHolder(
        private val binding: ItemHabitBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit, position: Int) {
            binding.apply {
                tvHabitName.text = habit.name
                tvHabitDate.text = "Created: ${habit.createdDate}"
                
                // Update completion status
                if (habit.isCompleted) {
                    cbHabitComplete.isChecked = true
                    tvHabitStatus.text = "✅ Completed"
                    tvHabitStatus.setTextColor(
                        root.context.getColor(android.R.color.holo_green_dark)
                    )
                } else {
                    cbHabitComplete.isChecked = false
                    tvHabitStatus.text = "⏳ Pending"
                    tvHabitStatus.setTextColor(
                        root.context.getColor(android.R.color.holo_orange_dark)
                    )
                }

                // Click listeners
                root.setOnClickListener {
                    listener.onHabitClick(habit, position)
                }

                root.setOnLongClickListener {
                    listener.onHabitLongClick(habit, position)
                    true
                }

                cbHabitComplete.setOnClickListener {
                    listener.onHabitClick(habit, position)
                }
            }
        }
    }
}