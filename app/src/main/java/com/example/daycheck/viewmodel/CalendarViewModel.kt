package com.example.daycheck.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.daycheck.data.models.Note
import com.example.daycheck.data.repository.NoteRepository
import kotlinx.coroutines.launch
// CalendarViewModel manages data for calendar, including dates with notes and searched notes.
class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NoteRepository(application)
    val datesWithNotes: LiveData<List<Long>> = repository.getDatesWithNotes()
    private val _searchedNotes = MutableLiveData<List<Note>>()
    val searchedNotes: LiveData<List<Note>> = _searchedNotes
    fun searchNotes(query: String, filter: String, isArchived: Boolean) {
        viewModelScope.launch {
            val notes = repository.searchNotes(query, isArchived).value ?: emptyList()
            val filtered = when (filter) {
                "completed" -> notes.filter { it.isCompleted }
                "incomplete" -> notes.filter { !it.isCompleted }
                else -> notes
            }
            _searchedNotes.postValue(filtered)
        }
    }
}