package com.example.daycheck.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.daycheck.data.models.ChecklistItem
import com.example.daycheck.data.models.Note
import com.example.daycheck.data.repository.NoteRepository
import kotlinx.coroutines.launch
// DayViewModel manages notes for a specific date, including insert, update, delete, and checklist operations.
class DayViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NoteRepository(application)
    private val _notesForDate = MutableLiveData<List<Note>>()
    val notesForDate: LiveData<List<Note>> = _notesForDate
    fun loadNotesForDate(date: Long) {
        repository.getNotesForDate(date).observeForever { notes ->
            _notesForDate.postValue(notes)
        }
    }
    fun insertNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note)
        }
    }
    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
    fun getNoteById(id: Int): LiveData<Note> = repository.getNoteById(id)
    fun insertChecklistItem(item: ChecklistItem) {
        viewModelScope.launch {
            repository.insertChecklistItem(item)
        }
    }
    fun updateChecklistItem(item: ChecklistItem) {
        viewModelScope.launch {
            repository.updateChecklistItem(item)
        }
    }
    fun getChecklistForNote(noteId: Int): LiveData<List<ChecklistItem>> = repository.getChecklistForNote(noteId)
}