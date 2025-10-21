package com.example.daycheck.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.daycheck.data.models.Note
import com.example.daycheck.data.repository.NoteRepository
import kotlinx.coroutines.launch
// ArchiveViewModel manages archived notes, search, and operations like restore/delete.
class ArchiveViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NoteRepository(application)
    private val _archivedNotes = MutableLiveData<List<Note>>()
    val archivedNotes: LiveData<List<Note>> = _archivedNotes
    fun loadArchivedNotes() {
        repository.getArchivedNotes().observeForever { notes ->
            _archivedNotes.postValue(notes)
        }
    }
    fun searchNotes(query: String, filter: String, isArchived: Boolean) {
        viewModelScope.launch {
            val notes = repository.searchNotes(query, isArchived).value ?: emptyList()
            val filtered = when (filter) {
                "completed" -> notes.filter { it.isCompleted }
                "incomplete" -> notes.filter { !it.isCompleted }
                else -> notes
            }
            _archivedNotes.postValue(filtered)
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
}