package com.daycheck.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.daycheck.app.DayCheckApplication
import com.daycheck.app.R
import com.daycheck.app.databinding.ActivityMainBinding
import com.daycheck.app.notifications.NotificationScheduler
import java.time.LocalDate

/**
 * Main activity that hosts the navigation and all fragments.
 * Uses bottom navigation to switch between Calendar, Archive, and Settings.
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationScheduler: NotificationScheduler
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize notification scheduler
        notificationScheduler = NotificationScheduler(this)
        
        setupNavigation()
        handleIntent(intent)
    }
    
    /**
     * Sets up the bottom navigation with the navigation controller.
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        binding.bottomNavigation.setupWithNavController(navController)
    }
    
    /**
     * Handles incoming intents, particularly from notifications.
     */
    private fun handleIntent(intent: Intent?) {
        if (intent?.hasExtra("note_id") == true) {
            val noteId = intent.getLongExtra("note_id", -1)
            if (noteId != -1L) {
                // Navigate to the specific note
                // This would typically involve finding the note's date and navigating to the day view
                // For now, we'll just navigate to the calendar
                binding.bottomNavigation.selectedItemId = R.id.nav_calendar
            }
        }
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    /**
     * Gets the application instance for dependency injection.
     */
    private fun getApp(): DayCheckApplication {
        return application as DayCheckApplication
    }
    
    /**
     * Gets the note repository from the application.
     */
    fun getNoteRepository() = getApp().noteRepository
    
    /**
     * Gets the notification scheduler.
     */
    fun getNotificationScheduler() = notificationScheduler
}