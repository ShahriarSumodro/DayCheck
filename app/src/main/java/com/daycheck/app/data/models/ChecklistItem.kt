package com.daycheck.app.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entity representing a single item in a note's checklist.
 * Each checklist item belongs to a specific note.
 */
@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val noteId: Long,
    val text: String,
    val isChecked: Boolean = false,
    val order: Int = 0
)