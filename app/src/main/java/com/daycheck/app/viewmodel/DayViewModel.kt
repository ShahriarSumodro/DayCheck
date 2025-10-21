package com.daycheck.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daycheck.app.data.models.ChecklistItem
import com.daycheck.app.data.models.Note
import com.daycheck.app.data.repository.NoteRepository
import com.daycheck.app.notifications.NotificationScheduler
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * ViewModel for the Day fragment.
 * Manages notes and checklist items for a specific date.
 */
class DayViewModel(
    private val repository: NoteRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    
    private val _currentDate = MutableLiveData<LocalDate>()
    val currentDate: LiveData<LocalDate> = _currentDate
    
    private val _activeNotes = MutableLiveData<List<Note>>()
    val activeNotes: LiveData<List<Note>> = _activeNotes
    
    private val _completedNotes = MutableLiveData<List<Note>>()
    val completedNotes: LiveData<List<Note>> = _completedNotes
    
    private val _checklistItems = MutableLiveData<Map<Long, List<ChecklistItem>>>()
    val checklistItems: LiveData<Map<Long, List<ChecklistItem>>> = _checklistItems
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    /**
     * Sets the current date and loads notes for that date.
     */
    fun setDate(date: LocalDate) {
        _currentDate.value = date
        loadNotesForDate(date)
    }
    
    /**
     * Loads all notes for the specified date.
     */
    private fun loadNotesForDate(date: LocalDate) {
        _isLoading.value = true
        
        viewModelScope.launch {
            // Load active notes
            repository.getActiveNotesForDate(date).collect { notes ->
                _activeNotes.value = notes
                loadChecklistItemsForNotes(notes)
            }
        }
        
        viewModelScope.launch {
            // Load completed notes
            repository.getCompletedNotesForDate(date).collect { notes ->
                _completedNotes.value = notes
            }
        }
        
        _isLoading.value = false
    }
    
    /**
     * Loads checklist items for a list of notes.
     */
    private fun loadChecklistItemsForNotes(notes: List<Note>) {
        val notesWithChecklists = notes.filter { it.hasChecklist }
        val checklistMap = mutableMapOf<Long, List<ChecklistItem>>()
        
        notesWithChecklists.forEach { note ->
            viewModelScope.launch {
                repository.getChecklistItemsForNote(note.id).collect { items ->
                    checklistMap[note.id] = items
                    _checklistItems.value = checklistMap.toMap()
                }
            }
        }
    }
    
    /**
     * Creates a new note.
     */
    fun createNote(
        title: String?,
        body: String,
        link: String?,
        hasChecklist: Boolean,
        reminderTimestamp: Long?,
        checklistItems: List<String>
    ) {
        val date = _currentDate.value ?: return
        
        viewModelScope.launch {
            val note = com.daycheck.app.data.models.Note(
                title = title?.takeIf { it.isNotBlank() },
                body = body,
                link = link?.takeIf { it.isNotBlank() },
                date = date,
                hasChecklist = hasChecklist,
                reminderTimestamp = reminderTimestamp
            )
            
            val noteId = repository.insertNote(note)
            
            // Add checklist items if any
            if (hasChecklist && checklistItems.isNotEmpty()) {
                val items = checklistItems.mapIndexed { index, text ->
                    ChecklistItem(
                        noteId = noteId,
                        text = text,
                        order = index
                    )
                }
                repository.insertChecklistItems(items)
            }
            
            // Schedule notification if reminder is set
            if (reminderTimestamp != null) {
                val updatedNote = note.copy(id = noteId)
                notificationScheduler.scheduleReminder(updatedNote)
            }
            
            // Refresh the notes
            loadNotesForDate(date)
        }
    }
    
    /**
     * Updates an existing note.
     */
    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
            loadNotesForDate(note.date)
        }
    }
    
    /**
     * Deletes a note.
     */
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            // Cancel notification if exists
            if (note.reminderTimestamp != null) {
                notificationScheduler.cancelReminder(note.id)
            }
            
            repository.deleteNote(note)
            loadNotesForDate(note.date)
        }
    }
    
    /**
     * Toggles the completion status of a note.
     */
    fun toggleNoteCompletion(note: Note) {
        viewModelScope.launch {
            val isCompleted = !note.isCompleted
            repository.updateNoteCompletion(note.id, isCompleted)
            
            // If completing, archive the note
            if (isCompleted) {
                repository.archiveNote(note.id)
            }
            
            loadNotesForDate(note.date)
        }
    }
    
    /**
     * Toggles the checked status of a checklist item.
     */
    fun toggleChecklistItem(item: ChecklistItem) {
        viewModelScope.launch {
            repository.updateItemCheckedStatus(item.id, !item.isChecked)
            
            // Check if all items are completed
            val noteId = item.noteId
            repository.getChecklistItemsForNote(noteId).collect { items ->
                val allChecked = items.all { it.isChecked }
                if (allChecked && items.isNotEmpty()) {
                    // Mark the note as completed
                    repository.updateNoteCompletion(noteId, true)
                    repository.archiveNote(noteId)
                }
            }
        }
    }
    
    /**
     * Refreshes the current day's data.
     */
    fun refresh() {
        _currentDate.value?.let { loadNotesForDate(it) }
    }
}