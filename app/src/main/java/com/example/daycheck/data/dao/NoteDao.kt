package com.example.daycheck.data.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.daycheck.data.models.Note
// NoteDao provides queries for notes, including by date, archived, search, etc.
@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long
    @Update
    suspend fun update(note: Note)
    @Delete
    suspend fun delete(note: Note)
    @Query("SELECT * FROM notes WHERE date = :date AND isArchived = 0 ORDER BY createdAt DESC")
    fun getNotesForDate(date: Long): LiveData<List<Note>>
    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): LiveData<Note>
    @Query("SELECT DISTINCT date FROM notes WHERE isArchived = 0")
    fun getDatesWithNotes(): LiveData<List<Long>>
    @Query("SELECT * FROM notes WHERE (isCompleted = 1 OR isArchived = 1) ORDER BY date DESC")
    fun getArchivedNotes(): LiveData<List<Note>>
    @Query("SELECT * FROM notes WHERE (title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%') AND isArchived = :isArchived")
    fun searchNotes(query: String, isArchived: Boolean): LiveData<List<Note>>
}