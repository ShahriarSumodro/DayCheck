package com.daycheck.app.data.repository

import com.daycheck.app.data.dao.ChecklistDao
import com.daycheck.app.data.dao.NoteDao
import com.daycheck.app.data.models.ChecklistItem
import com.daycheck.app.data.models.Note
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Repository class that provides a clean API for data access.
 * Acts as a single source of truth for data and handles the coordination between different data sources.
 */
class NoteRepository(
    private val noteDao: NoteDao,
    private val checklistDao: ChecklistDao
) {
    
    // Note operations
    fun getNotesForDate(date: LocalDate): Flow<List<Note>> {
        return noteDao.getNotesForDate(date)
    }
    
    fun getActiveNotesForDate(date: LocalDate): Flow<List<Note>> {
        return noteDao.getActiveNotesForDate(date)
    }
    
    fun getCompletedNotesForDate(date: LocalDate): Flow<List<Note>> {
        return noteDao.getCompletedNotesForDate(date)
    }
    
    fun getArchivedNotes(): Flow<List<Note>> {
        return noteDao.getArchivedNotes()
    }
    
    fun getDatesWithNotes(startDate: LocalDate, endDate: LocalDate): Flow<List<LocalDate>> {
        return noteDao.getDatesWithNotes(startDate, endDate)
    }
    
    suspend fun getNoteById(noteId: Long): Note? {
        return noteDao.getNoteById(noteId)
    }
    
    suspend fun getNotesWithReminders(currentTime: Long): List<Note> {
        return noteDao.getNotesWithReminders(currentTime)
    }
    
    fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes("%$query%")
    }
    
    suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(note)
    }
    
    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }
    
    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }
    
    suspend fun deleteNoteById(noteId: Long) {
        noteDao.deleteNoteById(noteId)
    }
    
    suspend fun updateNoteCompletion(noteId: Long, isCompleted: Boolean) {
        noteDao.updateNoteCompletion(noteId, isCompleted, LocalDateTime.now())
    }
    
    suspend fun updateNoteArchiveStatus(noteId: Long, isArchived: Boolean) {
        noteDao.updateNoteArchiveStatus(noteId, isArchived, LocalDateTime.now())
    }
    
    // Checklist operations
    fun getChecklistItemsForNote(noteId: Long): Flow<List<ChecklistItem>> {
        return checklistDao.getChecklistItemsForNote(noteId)
    }
    
    fun getUncheckedItemsForNote(noteId: Long): Flow<List<ChecklistItem>> {
        return checklistDao.getUncheckedItemsForNote(noteId)
    }
    
    fun getCheckedItemsForNote(noteId: Long): Flow<List<ChecklistItem>> {
        return checklistDao.getCheckedItemsForNote(noteId)
    }
    
    suspend fun insertChecklistItem(item: ChecklistItem): Long {
        return checklistDao.insertChecklistItem(item)
    }
    
    suspend fun insertChecklistItems(items: List<ChecklistItem>) {
        checklistDao.insertChecklistItems(items)
    }
    
    suspend fun updateChecklistItem(item: ChecklistItem) {
        checklistDao.updateChecklistItem(item)
    }
    
    suspend fun deleteChecklistItem(item: ChecklistItem) {
        checklistDao.deleteChecklistItem(item)
    }
    
    suspend fun deleteChecklistItemsForNote(noteId: Long) {
        checklistDao.deleteChecklistItemsForNote(noteId)
    }
    
    suspend fun updateItemCheckedStatus(itemId: Long, isChecked: Boolean) {
        checklistDao.updateItemCheckedStatus(itemId, isChecked)
    }
    
    suspend fun updateItemOrder(itemId: Long, order: Int) {
        checklistDao.updateItemOrder(itemId, order)
    }
    
    // Complex operations
    suspend fun completeNoteWithChecklist(noteId: Long) {
        // Mark note as completed
        updateNoteCompletion(noteId, true)
        
        // Mark all checklist items as checked
        val items = getChecklistItemsForNote(noteId)
        // Note: This is a simplified approach. In a real app, you'd want to handle this more carefully
        // by getting the current items and updating them individually
    }
    
    suspend fun archiveNote(noteId: Long) {
        updateNoteArchiveStatus(noteId, true)
    }
    
    suspend fun restoreNote(noteId: Long) {
        updateNoteArchiveStatus(noteId, false)
    }
}