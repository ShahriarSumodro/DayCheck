package com.daycheck.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daycheck.app.R
import com.daycheck.app.databinding.ItemCalendarDayBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Adapter for displaying calendar days in a grid layout.
 * Shows dates with indicators for days that have notes.
 */
class CalendarAdapter(
    private val onDateClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarDayViewHolder>() {
    
    private var calendarDays = listOf<LocalDate>()
    private var datesWithNotes = setOf<LocalDate>()
    private var currentMonth = LocalDate.now().withDayOfMonth(1)
    
    /**
     * Updates the calendar days to display.
     */
    fun updateCalendarDays(days: List<LocalDate>) {
        calendarDays = days
        currentMonth = days.firstOrNull()?.withDayOfMonth(1) ?: LocalDate.now().withDayOfMonth(1)
        notifyDataSetChanged()
    }
    
    /**
     * Updates the set of dates that have notes.
     */
    fun updateDatesWithNotes(dates: Set<LocalDate>) {
        datesWithNotes = dates
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalendarDayViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: CalendarDayViewHolder, position: Int) {
        val date = calendarDays[position]
        holder.bind(date)
    }
    
    override fun getItemCount(): Int = calendarDays.size
    
    /**
     * ViewHolder for individual calendar day items.
     */
    inner class CalendarDayViewHolder(
        private val binding: ItemCalendarDayBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(date: LocalDate) {
            val formatter = DateTimeFormatter.ofPattern("d")
            binding.dateText.text = date.format(formatter)
            
            // Show notes indicator if date has notes
            val hasNotes = datesWithNotes.contains(date)
            binding.notesIndicator.visibility = if (hasNotes) View.VISIBLE else View.GONE
            
            // Style the date based on whether it's in the current month
            val isCurrentMonth = date.month == currentMonth.month && date.year == currentMonth.year
            val isToday = date == LocalDate.now()
            
            when {
                isToday -> {
                    binding.dateText.setTextColor(binding.root.context.getColor(R.color.primary))
                    binding.dateText.textSize = 18f
                    binding.dateText.setTypeface(null, android.graphics.Typeface.BOLD)
                }
                isCurrentMonth -> {
                    binding.dateText.setTextColor(binding.root.context.getColor(R.color.text_primary))
                    binding.dateText.textSize = 16f
                    binding.dateText.setTypeface(null, android.graphics.Typeface.NORMAL)
                }
                else -> {
                    binding.dateText.setTextColor(binding.root.context.getColor(R.color.text_hint))
                    binding.dateText.textSize = 16f
                    binding.dateText.setTypeface(null, android.graphics.Typeface.NORMAL)
                }
            }
            
            // Set click listener
            binding.root.setOnClickListener {
                onDateClick(date)
            }
        }
    }
}