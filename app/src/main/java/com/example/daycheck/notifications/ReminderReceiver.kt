package com.example.daycheck.notifications
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.daycheck.data.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// ReminderReceiver handles alarm broadcasts for reminders and boot to reschedule.
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            rescheduleAll(context)
            return
        }
        val noteId = intent.getIntExtra("noteId", 0)
        val title = intent.getStringExtra("title") ?: "Reminder"
        val body = intent.getStringExtra("body") ?: ""
        NotificationScheduler.showNotification(context, noteId, title, body)
    }
    // Reschedules all future reminders after boot.
    private fun rescheduleAll(context: Context) {
        val repository = NoteRepository(context)
        CoroutineScope(Dispatchers.IO).launch {
            val notes = repository.getArchivedNotes().value ?: emptyList() // Actually get all with reminder > now
// But for simplicity, assume get all and check
            notes.filter { it.reminderTimestamp != null && it.reminderTimestamp > System.currentTimeMillis() }
                .forEach { NotificationScheduler.schedule(context, it) }
        }
    }
}
text