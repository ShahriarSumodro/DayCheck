package com.example.daycheck.data.db
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.daycheck.data.dao.ChecklistDao
import com.example.daycheck.data.dao.NoteDao
import com.example.daycheck.data.models.ChecklistItem
import com.example.daycheck.data.models.Note
// AppDatabase defines the Room database with Note and ChecklistItem entities.
// Version 1, no export schema for simplicity.
@Database(entities = [Note::class, ChecklistItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun checklistDao(): ChecklistDao
}