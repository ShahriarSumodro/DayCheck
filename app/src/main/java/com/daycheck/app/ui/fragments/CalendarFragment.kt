package com.daycheck.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.daycheck.app.R
import com.daycheck.app.databinding.FragmentCalendarBinding
import com.daycheck.app.ui.adapters.CalendarAdapter
import com.daycheck.app.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * Fragment that displays the monthly calendar view.
 * Shows a grid of dates with indicators for dates that have notes.
 */
class CalendarFragment : Fragment() {
    
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CalendarViewModel by activityViewModels()
    private lateinit var calendarAdapter: CalendarAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
    
    /**
     * Sets up the calendar RecyclerView with a grid layout.
     */
    private fun setupRecyclerView() {
        calendarAdapter = CalendarAdapter { date ->
            // Navigate to day view when a date is clicked
            val action = CalendarFragmentDirections.actionNavCalendarToNavDay(date.toString())
            findNavController().navigate(action)
        }
        
        binding.calendarGrid.apply {
            layoutManager = GridLayoutManager(context, 7)
            adapter = calendarAdapter
        }
    }
    
    /**
     * Sets up click listeners for navigation buttons and FAB.
     */
    private fun setupClickListeners() {
        binding.btnPreviousMonth.setOnClickListener {
            viewModel.goToPreviousMonth()
        }
        
        binding.btnNextMonth.setOnClickListener {
            viewModel.goToNextMonth()
        }
        
        binding.fabAddNote.setOnClickListener {
            // Navigate to today's day view to create a note
            val today = LocalDate.now().toString()
            val action = CalendarFragmentDirections.actionNavCalendarToNavDay(today)
            findNavController().navigate(action)
        }
    }
    
    /**
     * Observes the ViewModel for data changes.
     */
    private fun observeViewModel() {
        // Observe current month changes
        viewModel.currentMonth.observe(viewLifecycleOwner, Observer { month ->
            updateMonthDisplay(month)
            loadCalendarData(month)
        })
        
        // Observe dates with notes
        viewModel.datesWithNotes.observe(viewLifecycleOwner, Observer { dates ->
            calendarAdapter.updateDatesWithNotes(dates)
        })
    }
    
    /**
     * Updates the month/year display in the toolbar.
     */
    private fun updateMonthDisplay(month: YearMonth) {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        binding.monthYearText.text = month.format(formatter)
    }
    
    /**
     * Loads calendar data for the specified month.
     */
    private fun loadCalendarData(month: YearMonth) {
        val startDate = month.atDay(1)
        val endDate = month.atEndOfMonth()
        
        // Generate calendar days for the month
        val calendarDays = generateCalendarDays(month)
        calendarAdapter.updateCalendarDays(calendarDays)
    }
    
    /**
     * Generates a list of calendar days for the month, including padding days.
     */
    private fun generateCalendarDays(month: YearMonth): List<LocalDate> {
        val days = mutableListOf<LocalDate>()
        
        // Add padding days from previous month
        val firstDayOfMonth = month.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
        val paddingDays = if (firstDayOfWeek == 7) 0 else firstDayOfWeek
        
        for (i in paddingDays - 1 downTo 0) {
            days.add(firstDayOfMonth.minusDays(i.toLong()))
        }
        
        // Add days of the current month
        for (day in 1..month.lengthOfMonth()) {
            days.add(month.atDay(day))
        }
        
        // Add padding days from next month to complete the grid
        val totalDays = days.size
        val remainingDays = 42 - totalDays // 6 weeks * 7 days
        val lastDayOfMonth = month.atEndOfMonth()
        
        for (day in 1..remainingDays) {
            days.add(lastDayOfMonth.plusDays(day.toLong()))
        }
        
        return days
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}