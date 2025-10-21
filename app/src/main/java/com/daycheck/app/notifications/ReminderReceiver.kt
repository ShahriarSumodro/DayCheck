package com.daycheck.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.daycheck.app.R
import com.daycheck.app.ui.MainActivity

/**
 * BroadcastReceiver that handles reminder notifications.
 * Triggered by AlarmManager when a note's reminder time is reached.
 */
class ReminderReceiver : BroadcastReceiver() {
    
    companion object {
        const val EXTRA_NOTE_ID = "note_id"
        const val EXTRA_NOTE_TITLE = "note_title"
        const val EXTRA_NOTE_BODY = "note_body"
        const val CHANNEL_ID = "reminder_channel"
        const val NOTIFICATION_ID = 1001
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1)
        val title = intent.getStringExtra(EXTRA_NOTE_TITLE) ?: "Reminder"
        val body = intent.getStringExtra(EXTRA_NOTE_BODY) ?: ""
        
        if (noteId == -1L) return
        
        createNotificationChannel(context)
        showNotification(context, noteId, title, body)
    }
    
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for note reminders"
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun showNotification(context: Context, noteId: Long, title: String, body: String) {
        // Create intent to open the app to the specific note
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("note_id", noteId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(noteId.toInt(), notification)
    }
}