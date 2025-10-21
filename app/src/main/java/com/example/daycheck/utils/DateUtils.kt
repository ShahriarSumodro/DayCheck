package com.example.daycheck.utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
// DateUtils provides helper functions for date formatting and month days calculation.
object DateUtils {
    fun formatDate(millis: Long): String {
        return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(millis)
    }
    fun formatDateTime(millis: Long): String {
        return SimpleDateFormat("MMMM dd, yyyy HH:mm", Locale.getDefault()).format(millis)
    }
    fun formatMonthYear(millis: Long): String {
        return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(millis)
    }
    fun getDayOfMonth(millis: Long): Int {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        return cal.get(Calendar.DAY_OF_MONTH)
    }
    fun getDaysInMonth(calendar: Calendar): List<Long> {
        val days = mutableListOf<Long>()
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
        for (i in 0 until firstDayOfWeek) {
            days.add(0L) // Empty days
        }
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..maxDay) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            days.add(getStartOfDay(cal.timeInMillis))
        }
        return days
    }
    fun getStartOfDay(millis: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}