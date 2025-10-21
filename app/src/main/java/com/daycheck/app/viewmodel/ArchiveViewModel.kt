package com.daycheck.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daycheck.app.data.models.Note
import com.daycheck.app.data.repository.NoteRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the Archive fragment.
 * Manages archived/completed notes.
 */
class ArchiveViewModel(private val repository: NoteRepository) : ViewModel() {
    
    private val _archivedNotes = MutableLiveData<List<Note>>()
    val archivedNotes: LiveData<List<Note>> = _archivedNotes
    
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _filterType = MutableLiveData<FilterType>()
    val filterType: LiveData<FilterType> = _filterType
    
    init {
        _filterType.value = FilterType.ALL
        loadArchivedNotes()
    }
    
    /**
     * Loads archived notes from the repository.
     */
    private fun loadArchivedNotes() {
        _isLoading.value = true
        
        viewModelScope.launch {
            repository.getArchivedNotes().collect { notes ->
                val filteredNotes = applyFilters(notes)
                _archivedNotes.value = filteredNotes
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Applies search and filter criteria to the notes list.
     */
    private fun applyFilters(notes: List<Note>): List<Note> {
        var filteredNotes = notes
        
        // Apply search filter
        val query = _searchQuery.value
        if (!query.isNullOrBlank()) {
            filteredNotes = filteredNotes.filter { note ->
                note.title?.contains(query, ignoreCase = true) == true ||
                note.body.contains(query, ignoreCase = true)
            }
        }
        
        // Apply type filter
        when (_filterType.value) {
            FilterType.COMPLETED -> filteredNotes = filteredNotes.filter { it.isCompleted }
            FilterType.INCOMPLETE -> filteredNotes = filteredNotes.filter { !it.isCompleted }
            FilterType.ALL -> { /* No additional filtering */ }
            null -> { /* No additional filtering */ }
        }
        
        return filteredNotes
    }
    
    /**
     * Sets the search query and refreshes the list.
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        loadArchivedNotes()
    }
    
    /**
     * Sets the filter type and refreshes the list.
     */
    fun setFilterType(filterType: FilterType) {
        _filterType.value = filterType
        loadArchivedNotes()
    }
    
    /**
     * Restores a note from archive (moves it back to its original date).
     */
    fun restoreNote(note: Note) {
        viewModelScope.launch {
            repository.restoreNote(note.id)
            repository.updateNoteCompletion(note.id, false)
            loadArchivedNotes()
        }
    }
    
    /**
     * Permanently deletes a note from archive.
     */
    fun deleteNotePermanently(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
            loadArchivedNotes()
        }
    }
    
    /**
     * Clears all archived notes (permanent deletion).
     */
    fun clearAllArchivedNotes() {
        viewModelScope.launch {
            val notes = _archivedNotes.value ?: emptyList()
            notes.forEach { note ->
                repository.deleteNote(note)
            }
            loadArchivedNotes()
        }
    }
    
    /**
     * Refreshes the archived notes list.
     */
    fun refresh() {
        loadArchivedNotes()
    }
    
    /**
     * Enum for filter types in the archive.
     */
    enum class FilterType {
        ALL, COMPLETED, INCOMPLETE
    }
}