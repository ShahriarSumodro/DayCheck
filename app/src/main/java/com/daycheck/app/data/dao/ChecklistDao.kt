package com.daycheck.app.data.dao

import androidx.room.*
import com.daycheck.app.data.models.ChecklistItem
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ChecklistItem entity.
 * Provides methods to interact with the checklist_items table in the database.
 */
@Dao
interface ChecklistDao {
    
    @Query("SELECT * FROM checklist_items WHERE noteId = :noteId ORDER BY `order` ASC")
    fun getChecklistItemsForNote(noteId: Long): Flow<List<ChecklistItem>>
    
    @Query("SELECT * FROM checklist_items WHERE noteId = :noteId AND isChecked = 0 ORDER BY `order` ASC")
    fun getUncheckedItemsForNote(noteId: Long): Flow<List<ChecklistItem>>
    
    @Query("SELECT * FROM checklist_items WHERE noteId = :noteId AND isChecked = 1 ORDER BY `order` ASC")
    fun getCheckedItemsForNote(noteId: Long): Flow<List<ChecklistItem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: ChecklistItem): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<ChecklistItem>)
    
    @Update
    suspend fun updateChecklistItem(item: ChecklistItem)
    
    @Delete
    suspend fun deleteChecklistItem(item: ChecklistItem)
    
    @Query("DELETE FROM checklist_items WHERE noteId = :noteId")
    suspend fun deleteChecklistItemsForNote(noteId: Long)
    
    @Query("UPDATE checklist_items SET isChecked = :isChecked WHERE id = :itemId")
    suspend fun updateItemCheckedStatus(itemId: Long, isChecked: Boolean)
    
    @Query("UPDATE checklist_items SET `order` = :order WHERE id = :itemId")
    suspend fun updateItemOrder(itemId: Long, order: Int)
}