package com.example.daycheck.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.daycheck.databinding.ItemCalendarDayBinding
import com.example.daycheck.utils.DateUtils
import java.util.Calendar

// CalendarAdapter displays days in a month grid, with green dot for dates with notes.
// Days are tappable to open day view.
class CalendarAdapter(private val onDateClick: (Long) -> Unit) : ListAdapter<Long, CalendarAdapter.ViewHolder>(DiffCallback()) {

    private val datesWithNotes = mutableSetOf<Long>()

    class ViewHolder(private val binding: ItemCalendarDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(day: Long, hasNote: Boolean, onClick: (Long) -> Unit) {
            binding.dayText.text = DateUtils.getDayOfMonth(day).toString()
            binding.dot.visibility = if (hasNote) ViewGroup.VISIBLE else ViewGroup.GONE
            binding.root.setOnClickListener { onClick(day) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = getItem(position)
        holder.bind(day, datesWithNotes.contains(day), onDateClick)
    }

    // Sets dates with notes for marking.
    fun setDatesWithNotes(dates: List<Long>) {
        datesWithNotes.clear()
        datesWithNotes.addAll(dates)
        notifyDataSetChanged()
    }

    class DiffCallback : DiffUtil.ItemCallback<Long>() {
        override fun areItemsTheSame(oldItem: Long, newItem: Long): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: Long, newItem: Long): Boolean = oldItem == newItem
    }
}