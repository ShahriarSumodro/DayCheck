package com.example.daycheck

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.daycheck.databinding.ActivityMainBinding
import com.example.daycheck.notifications.NotificationScheduler
import com.google.android.material.bottomnavigation.BottomNavigationView

// MainActivity is the single activity hosting all fragments via Navigation Component.
// It sets up bottom navigation, handles deep links for notifications, and initializes app-wide settings like dark mode and notification channel.
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Follow system dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        // Set up navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav: BottomNavigationView = binding.bottomNav
        bottomNav.labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_UNLABELED
        bottomNav.setupWithNavController(navController)

        createNotificationChannel()

        // Handle intent from notification
        handleNotificationIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    // Creates the notification channel for reminders (required for Android O+).
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationScheduler.CHANNEL_ID,
                "Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for note reminders"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Handles intents from notifications to navigate to the specific day view.
    private fun handleNotificationIntent(intent: Intent?) {
        intent?.let {
            val date = it.getLongExtra("date", 0L)
            val noteId = it.getIntExtra("noteId", 0)
            if (date != 0L) {
                val bundle = Bundle().apply {
                    putLong("date", date)
                    putInt("noteId", noteId)
                }
                navController.navigate(R.id.dayFragment, bundle)
            }
        }
    }
}