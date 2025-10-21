package com.daycheck.app.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.daycheck.app.data.dao.ChecklistDao
import com.daycheck.app.data.dao.NoteDao
import com.daycheck.app.data.models.ChecklistItem
import com.daycheck.app.data.models.Converters
import com.daycheck.app.data.models.Note

/**
 * Room database for the Day Check app.
 * Contains tables for notes and checklist items with proper relationships.
 */
@Database(
    entities = [Note::class, ChecklistItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun noteDao(): NoteDao
    abstract fun checklistDao(): ChecklistDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daycheck_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}