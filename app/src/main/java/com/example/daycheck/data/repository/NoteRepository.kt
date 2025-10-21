package com.example.daycheck.data.repository
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.daycheck.data.db.AppDatabase
import com.example.daycheck.data.models.ChecklistItem
import com.example.daycheck.data.models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
// NoteRepository provides a single source for data operations using DAOs.
// Uses coroutines for suspend functions.
class NoteRepository(context: Context) {
    private val db: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "daycheck_db"
    ).build()
    private val noteDao = db.noteDao()
    private val checklistDao = db.checklistDao()
    suspend fun insertNote(note: Note): Long = withContext(Dispatchers.IO) {
        noteDao.insert(note)
    }
    suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        noteDao.update(note)
    }
    suspend fun deleteNote(note: Note) = withContext(Dispatchers.IO) {
        noteDao.delete(note)
    }
    fun getNotesForDate(date: Long): LiveData<List<Note>> = noteDao.getNotesForDate(date)
    fun getNoteById(id: Int): LiveData<Note> = noteDao.getNoteById(id)
    fun getDatesWithNotes(): LiveData<List<Long>> = noteDao.getDatesWithNotes()
    fun getArchivedNotes(): LiveData<List<Note>> = noteDao.getArchivedNotes()
    fun searchNotes(query: String, isArchived: Boolean): LiveData<List<Note>> = noteDao.searchNotes(query, isArchived)
    suspend fun insertChecklistItem(item: ChecklistItem) = withContext(Dispatchers.IO) {
        checklistDao.insert(item)
    }
    suspend fun updateChecklistItem(item: ChecklistItem) = withContext(Dispatchers.IO) {
        checklistDao.update(item)
    }
    suspend fun deleteChecklistItem(item: ChecklistItem) = withContext(Dispatchers.IO) {
        checklistDao.delete(item)
    }
    fun getChecklistForNote(noteId: Int): LiveData<List<ChecklistItem>> = checklistDao.getChecklistForNote(noteId)
    suspend fun clearData() = withContext(Dispatchers.IO) {
        db.clearAllTables()
    }
}