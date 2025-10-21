package com.example.daycheck.data.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.daycheck.data.models.ChecklistItem
// ChecklistDao provides operations for checklist items.
@Dao
interface ChecklistDao {
    @Insert
    suspend fun insert(item: ChecklistItem)
    @Update
    suspend fun update(item: ChecklistItem)
    @Delete
    suspend fun delete(item: ChecklistItem)
    @Query("SELECT * FROM checklist_items WHERE noteId = :noteId ORDER BY id")
    fun getChecklistForNote(noteId: Int): LiveData<List<ChecklistItem>>
}