package com.abdurrahmanjun.runingapp.ui.home

import com.abdurrahmanjun.runingapp.data.local.entity.RunEntity
import com.abdurrahmanjun.runingapp.utils.UnitFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

/** Aggregated weekly progress for the Home hero card. */
data class WeeklySummary(
    val distanceValue: String,     // e.g. "18.4"
    val distanceUnit: String,      // e.g. "km"
    val runCount: Int,
    val goalValue: String,         // goal in display units, e.g. "24"
    val remainingText: String,     // "5.6 km to your weekly goal of 24 km" / goal-reached text
    val progress: Float,           // 0f..1f
    val weekOverWeekPercent: Int?, // +12 / -8 / null when no prior-week data
)

/** One row in the recent-runs list. */
data class RunListItem(
    val id: Int?,
    val title: String,
    val subtitle: String,          // "Today · 32:14 · 5'12"/km"
    val distanceValue: String,     // "6.2"
    val distanceUnit: String,      // "km"
)

object HomeMapper {

    fun greeting(hour: Int): String = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "Hello"
    }

    fun initials(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotEmpty() }
        return when {
            parts.isEmpty() -> "?"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "${parts.first().first()}${parts.last().first()}".uppercase()
        }
    }

    /** Title derived from the time of day the run started (agreed derivation, no schema change). */
    private fun titleForHour(hour: Int): String = when (hour) {
        in 5..11 -> "Morning run"
        in 12..16 -> "Afternoon run"
        in 17..21 -> "Evening run"
        else -> "Night run"
    }

    private fun relativeDay(timestamp: Long, now: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val today = Calendar.getInstance().apply { timeInMillis = now }
        fun dayKey(c: Calendar) = c.get(Calendar.YEAR) * 1000 + c.get(Calendar.DAY_OF_YEAR)
        val diff = dayKey(today) - dayKey(cal)
        return when {
            diff == 0 -> "Today"
            diff == 1 -> "Yesterday"
            diff in 2..6 -> SimpleDateFormat("EEE", Locale.getDefault()).format(cal.time)
            else -> SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(cal.time)
        }
    }

    fun toRunItem(run: RunEntity, fmt: UnitFormatter, now: Long): RunListItem {
        val cal = Calendar.getInstance().apply { timeInMillis = run.timestamp }
        val day = relativeDay(run.timestamp, now)
        val clock = fmt.clock(run.timeInMillis)
        val pace = fmt.pace(run.distanceInMeters, run.timeInMillis)
        return RunListItem(
            id = run.id,
            title = titleForHour(cal.get(Calendar.HOUR_OF_DAY)),
            subtitle = "$day · $clock · $pace${fmt.paceUnit}",
            distanceValue = fmt.distanceValue(run.distanceInMeters),
            distanceUnit = fmt.distanceUnit,
        )
    }

    private fun weekStart(now: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }
        // If snapping to firstDayOfWeek jumped ahead of now, step back a week.
        if (cal.timeInMillis > now) cal.add(Calendar.DAY_OF_YEAR, -7)
        return cal.timeInMillis
    }

    fun weeklySummary(runs: List<RunEntity>, fmt: UnitFormatter, goalKm: Float, now: Long): WeeklySummary {
        val weekStart = weekStart(now)
        val prevWeekStart = weekStart - 7 * 24 * 60 * 60 * 1000L
        val thisWeek = runs.filter { it.timestamp >= weekStart }
        val prevWeek = runs.filter { it.timestamp in prevWeekStart until weekStart }

        val thisMeters = thisWeek.sumOf { it.distanceInMeters }
        val prevMeters = prevWeek.sumOf { it.distanceInMeters }
        val goalMeters = (goalKm * 1000f)
        val remainingMeters = (goalMeters - thisMeters).coerceAtLeast(0f)

        val wow = if (prevMeters > 0) {
            ((thisMeters - prevMeters) * 100f / prevMeters).roundToInt()
        } else null

        val remainingText = if (remainingMeters <= 0f) {
            "Weekly goal of ${fmt.kmValue(goalKm)} ${fmt.distanceUnit} reached 🎉"
        } else {
            "${fmt.distanceValue(remainingMeters.toInt())} ${fmt.distanceUnit} to your weekly goal of ${fmt.kmValue(goalKm)} ${fmt.distanceUnit}"
        }

        return WeeklySummary(
            distanceValue = fmt.distanceValue(thisMeters),
            distanceUnit = fmt.distanceUnit,
            runCount = thisWeek.size,
            goalValue = fmt.kmValue(goalKm),
            remainingText = remainingText,
            progress = if (goalMeters > 0) (thisMeters / goalMeters).coerceIn(0f, 1f) else 0f,
            weekOverWeekPercent = wow,
        )
    }
}
