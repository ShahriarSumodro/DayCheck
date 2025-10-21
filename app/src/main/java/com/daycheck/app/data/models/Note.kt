package com.daycheck.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Entity representing a note or task in the Day Check app.
 * Each note belongs to a specific date and can contain text, links, and checklist items.
 */
@Entity(tableName = "notes")
@TypeConverters(Converters::class)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val title: String? = null,
    val body: String = "",
    val link: String? = null,
    
    // Date this note belongs to (not creation date)
    val date: LocalDate,
    
    // Checklist functionality
    val hasChecklist: Boolean = false,
    
    // Status flags
    val isCompleted: Boolean = false,
    val isArchived: Boolean = false,
    
    // Reminder functionality
    val reminderTimestamp: Long? = null,
    
    // Color for theming (optional)
    val color: String? = null,
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)