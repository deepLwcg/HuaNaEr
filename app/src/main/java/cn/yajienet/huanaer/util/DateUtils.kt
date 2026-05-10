package cn.yajienet.huanaer.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    private val monthYearFormat = SimpleDateFormat("yyyy年MM月", Locale.CHINA)

    /**
     * Get the start time (first day at 00:00:00) of a given month and year
     */
    fun getMonthStartTime(month: Int, year: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * Get the end time (last day at 23:59:59) of a given month and year
     */
    fun getMonthEndTime(month: Int, year: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.MONTH, 1)
        return calendar.timeInMillis
    }

    /**
     * Get the current month (1-12)
     */
    fun getCurrentMonth(): Int {
        return Calendar.getInstance().get(Calendar.MONTH) + 1
    }

    /**
     * Get the current year
     */
    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    /**
     * Format a timestamp to date string (yyyy-MM-dd)
     */
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(timestamp)
    }

    /**
     * Format a timestamp to date and time string (yyyy-MM-dd HH:mm)
     */
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(timestamp)
    }

    /**
     * Format month and year to display string (yyyy年MM月)
     */
    fun formatMonthYear(month: Int, year: Int): String {
        return "${year}年${month}月"
    }

    /**
     * Get current timestamp in milliseconds
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
}