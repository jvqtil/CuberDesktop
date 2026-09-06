package dev.jvqtil.cuber.desktop.util

import dev.jvqtil.cuber.desktop.database.PENALTY_PLUS_TWO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {
    fun format(ms: Long): String {
        val minutes = ms / 60_000
        val seconds = (ms / 1_000) % 60
        val centiseconds = (ms % 1_000) / 10

        return if (minutes > 0) {
            String.format(Locale.ROOT, "$minutes:%02d.%02d", seconds, centiseconds)
        } else {
            String.format(Locale.ROOT, "%d.%02d", seconds, centiseconds)
        }
    }

    fun formatNullable(ms: Long?): String =
        ms?.let(::format) ?: "—"

    fun formatSolveTime(time: Long, penalty: String): String =
        format(
            if (penalty == PENALTY_PLUS_TWO) time + 2_000L else time
        )

    fun formatTimeOfDay(timestamp: Long): String =
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

    fun formatDateTime(timestamp: Long): String =
        SimpleDateFormat("d MMMM yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))

    fun formatDate(timestamp: Long): String =
        SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(timestamp))
}
