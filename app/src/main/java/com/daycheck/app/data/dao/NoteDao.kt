package com.daycheck.app.data.dao

import androidx.room.*
import com.daycheck.app.data.models.Note
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for Note entity.
 * Provides methods to interact with the notes table in the database.
 */
@Dao
interface NoteDao {
    
    @Query("SELECT * FROM notes WHERE date = :date ORDER BY createdAt ASC")
    fun getNotesForDate(date: LocalDate): Flow<List<Note>>
    
    @Query("SELECT * FROM notes WHERE date = :date AND isCompleted = 0 ORDER BY createdAt ASC")
    fun getActiveNotesForDate(date: LocalDate): Flow<List<Note>>
    
    @Query("SELECT * FROM notes WHERE date = :date AND isCompleted = 1 ORDER BY createdAt ASC")
    fun getCompletedNotesForDate(date: LocalDate): Flow<List<Note>>
    
    @Query("SELECT * FROM notes WHERE isArchived = 1 ORDER BY updatedAt DESC")
    fun getArchivedNotes(): Flow<List<Note>>
    
    @Query("SELECT DISTINCT date FROM notes WHERE date >= :startDate AND date <= :endDate")
    fun getDatesWithNotes(startDate: LocalDate, endDate: LocalDate): Flow<List<LocalDate>>
    
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): Note?
    
    @Query("SELECT * FROM notes WHERE reminderTimestamp IS NOT NULL AND reminderTimestamp <= :currentTime")
    suspend fun getNotesWithReminders(currentTime: Long): List<Note>
    
    @Query("SELECT * FROM notes WHERE title LIKE :query OR body LIKE :query ORDER BY updatedAt DESC")
    fun searchNotes(query: String): Flow<List<Note>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long
    
    @Update
    suspend fun updateNote(note: Note)
    
    @Delete
    suspend fun deleteNote(note: Note)
    
    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long)
    
    @Query("UPDATE notes SET isCompleted = :isCompleted, updatedAt = :updatedAt WHERE id = :noteId")
    suspend fun updateNoteCompletion(noteId: Long, isCompleted: Boolean, updatedAt: java.time.LocalDateTime)
    
    @Query("UPDATE notes SET isArchived = :isArchived, updatedAt = :updatedAt WHERE id = :noteId")
    suspend fun updateNoteArchiveStatus(noteId: Long, isArchived: Boolean, updatedAt: java.time.LocalDateTime)
}