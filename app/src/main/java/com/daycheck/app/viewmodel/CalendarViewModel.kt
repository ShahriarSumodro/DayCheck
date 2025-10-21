package com.daycheck.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daycheck.app.data.repository.NoteRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

/**
 * ViewModel for the Calendar fragment.
 * Manages the calendar state and provides data for the monthly calendar view.
 */
class CalendarViewModel(private val repository: NoteRepository) : ViewModel() {
    
    private val _currentMonth = MutableLiveData<YearMonth>()
    val currentMonth: LiveData<YearMonth> = _currentMonth
    
    private val _datesWithNotes = MutableLiveData<Set<LocalDate>>()
    val datesWithNotes: LiveData<Set<LocalDate>> = _datesWithNotes
    
    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> = _selectedDate
    
    init {
        // Initialize with current month
        _currentMonth.value = YearMonth.now()
        loadDatesWithNotes()
    }
    
    /**
     * Loads dates that have notes for the current month.
     */
    private fun loadDatesWithNotes() {
        val month = _currentMonth.value ?: return
        val startDate = month.atDay(1)
        val endDate = month.atEndOfMonth()
        
        viewModelScope.launch {
            repository.getDatesWithNotes(startDate, endDate).collect { dates ->
                _datesWithNotes.value = dates.toSet()
            }
        }
    }
    
    /**
     * Navigates to the previous month.
     */
    fun goToPreviousMonth() {
        _currentMonth.value?.let { current ->
            _currentMonth.value = current.minusMonths(1)
            loadDatesWithNotes()
        }
    }
    
    /**
     * Navigates to the next month.
     */
    fun goToNextMonth() {
        _currentMonth.value?.let { current ->
            _currentMonth.value = current.plusMonths(1)
            loadDatesWithNotes()
        }
    }
    
    /**
     * Navigates to the current month (today).
     */
    fun goToCurrentMonth() {
        _currentMonth.value = YearMonth.now()
        loadDatesWithNotes()
    }
    
    /**
     * Selects a date on the calendar.
     */
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
    
    /**
     * Checks if a date has notes.
     */
    fun hasNotesForDate(date: LocalDate): Boolean {
        return _datesWithNotes.value?.contains(date) ?: false
    }
    
    /**
     * Refreshes the calendar data.
     */
    fun refreshCalendar() {
        loadDatesWithNotes()
    }
}