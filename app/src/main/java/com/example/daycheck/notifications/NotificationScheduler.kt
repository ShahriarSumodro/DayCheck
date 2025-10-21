package com.example.daycheck.notifications
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.daycheck.data.models.Note
import com.example.daycheck.MainActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
// NotificationScheduler schedules and cancels reminders using AlarmManager.
// Uses exact alarms for reliability.
object NotificationScheduler {
    const val CHANNEL_ID = "reminders_channel"
    fun schedule(context: Context, note: Note) {
        if (note.reminderTimestamp == null) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("noteId", note.id)
            putExtra("title", note.title ?: "Reminder")
            putExtra("body", note.body ?: "")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            note.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            note.reminderTimestamp,
            pendingIntent
        )
    }
    fun cancel(context: Context, noteId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
    // Shows the notification.
    fun showNotification(context: Context, noteId: Int, title: String, body: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("noteId", noteId)
// Date would be fetched from DB if needed, but for simplicity, assume
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            notify(noteId, builder.build())
        }
    }
}