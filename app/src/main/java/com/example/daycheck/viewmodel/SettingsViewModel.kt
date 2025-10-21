package com.example.daycheck.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.daycheck.data.repository.NoteRepository
import kotlinx.coroutines.launch
// SettingsViewModel handles clearing data.
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NoteRepository(application)
    fun clearData() {
        viewModelScope.launch {
            repository.clearData()
        }
    }
}