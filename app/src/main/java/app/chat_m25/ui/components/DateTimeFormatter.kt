package app.chat_m25.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateTimeFormatter {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val fullDateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val weekDayFormat = SimpleDateFormat("EEE", Locale.getDefault())

    fun formatChatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val oneDay = TimeUnit.DAYS.toMillis(1)

        return when {
            diff < oneDay -> timeFormat.format(Date(timestamp))
            diff < 7 * oneDay -> weekDayFormat.format(Date(timestamp))
            else -> dateFormat.format(Date(timestamp))
        }
    }

    fun formatMomentTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val oneMinute = TimeUnit.MINUTES.toMillis(1)
        val oneHour = TimeUnit.HOURS.toMillis(1)
        val oneDay = TimeUnit.DAYS.toMillis(1)

        return when {
            diff < oneMinute -> "刚刚"
            diff < oneHour -> "${diff / oneMinute}分钟前"
            diff < oneDay -> "${diff / oneHour}小时前"
            diff < 7 * oneDay -> "${diff / oneDay}天前"
            else -> dateFormat.format(Date(timestamp))
        }
    }

    fun formatFullDateTime(timestamp: Long): String {
        return fullDateTimeFormat.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }
}
