package com.daycheck.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver that handles device boot events.
 * Reschedules all reminders after device reboot to ensure they persist.
 */
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                // Reschedule all reminders in a coroutine
                CoroutineScope(Dispatchers.IO).launch {
                    val scheduler = NotificationScheduler(context)
                    scheduler.rescheduleAllReminders()
                }
            }
        }
    }
}
