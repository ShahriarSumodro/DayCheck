package com.example.daycheck.data.models
import androidx.room.Entity
import androidx.room.PrimaryKey
// ChecklistItem entity for individual items in a note's checklist.
@Entity(tableName = "checklist_items")
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val noteId: Int,
    val text: String,
    val isChecked: Boolean = false
)