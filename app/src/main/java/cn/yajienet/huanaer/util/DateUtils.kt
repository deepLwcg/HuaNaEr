package cn.yajienet.huanaer.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    // DateTimeFormatter 是线程安全的，可以作为静态变量使用
    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA)
    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.CHINA)
    private val zoneId = ZoneId.systemDefault()

    /**
     * Get the start time (first day at 00:00:00) of a given month and year
     */
    fun getMonthStartTime(month: Int, year: Int): Long {
        val firstDay = LocalDate.of(year, month, 1)
        return firstDay.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    /**
     * Get the end time (last day at 23:59:59) of a given month and year
     */
    fun getMonthEndTime(month: Int, year: Int): Long {
        val firstDayOfNextMonth = LocalDate.of(year, month, 1).plusMonths(1)
        return firstDayOfNextMonth.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    /**
     * Get the current month (1-12)
     */
    fun getCurrentMonth(): Int {
        return LocalDate.now().monthValue
    }

    /**
     * Get the current year
     */
    fun getCurrentYear(): Int {
        return LocalDate.now().year
    }

    /**
     * Format a timestamp to date string (yyyy-MM-dd)
     */
    fun formatDate(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDate = instant.atZone(zoneId).toLocalDate()
        return localDate.format(dateFormat)
    }

    /**
     * Format a timestamp to date and time string (yyyy-MM-dd HH:mm)
     */
    fun formatDateTime(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDateTime = instant.atZone(zoneId).toLocalDateTime()
        return localDateTime.format(dateTimeFormat)
    }

    /**
     * Format month and year to display string (yyyy/MM)
     */
    fun formatMonthYear(month: Int, year: Int): String {
        return "${year}/${month.toString().padStart(2, '0')}"
    }

    /**
     * Get current timestamp in milliseconds
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
}