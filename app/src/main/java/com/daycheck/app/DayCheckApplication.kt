package com.daycheck.app

import android.app.Application
import com.daycheck.app.data.db.AppDatabase
import com.daycheck.app.data.repository.NoteRepository

/**
 * Application class for Day Check app.
 * Initializes the database and repository for dependency injection.
 */
class DayCheckApplication : Application() {
    
    // Database and repository instances
    val database by lazy { AppDatabase.getDatabase(this) }
    val noteRepository by lazy { 
        NoteRepository(
            database.noteDao(),
            database.checklistDao()
        )
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: DayCheckApplication
            private set
    }
}