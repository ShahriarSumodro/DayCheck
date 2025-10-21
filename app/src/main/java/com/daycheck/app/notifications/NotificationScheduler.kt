package com.daycheck.app.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.daycheck.app.data.models.Note
import java.util.Calendar

/**
 * Handles scheduling and managing notifications for notes with reminders.
 * Uses AlarmManager for reliable notification delivery even when app is closed.
 */
class NotificationScheduler(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    /**
     * Schedules a notification for a note with a reminder.
     * @param note The note to schedule a reminder for
     */
    fun scheduleReminder(note: Note) {
        if (note.reminderTimestamp == null) return
        
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_NOTE_ID, note.id)
            putExtra(ReminderReceiver.EXTRA_NOTE_TITLE, note.title ?: "Reminder")
            putExtra(ReminderReceiver.EXTRA_NOTE_BODY, note.body)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            note.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Schedule the alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            note.reminderTimestamp,
            pendingIntent
        )
    }
    
    /**
     * Cancels a scheduled reminder for a note.
     * @param noteId The ID of the note to cancel the reminder for
     */
    fun cancelReminder(noteId: Long) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }
    
    /**
     * Reschedules all reminders after device reboot.
     * This is called by BootReceiver to ensure reminders persist across reboots.
     */
    suspend fun rescheduleAllReminders() {
        // This would typically fetch all notes with reminders from the database
        // and reschedule them. For now, we'll implement a placeholder.
        // In a real implementation, you'd inject the repository and fetch notes.
    }
}