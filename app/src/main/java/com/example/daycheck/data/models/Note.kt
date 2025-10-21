package com.example.daycheck.data.models
import androidx.room.Entity
import androidx.room.PrimaryKey
// Note entity represents a note or task with optional title, body, link, reminder, and checklist flag.
// Date is stored as long (midnight millis for the day).
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String? = null,
    val body: String? = null,
    val date: Long,
    val hasChecklist: Boolean = false,
    val isArchived: Boolean = false,
    val isCompleted: Boolean = false,
    val reminderTimestamp: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val link: String? = null,
    val color: String? = null // Hex color for note background, optional
)