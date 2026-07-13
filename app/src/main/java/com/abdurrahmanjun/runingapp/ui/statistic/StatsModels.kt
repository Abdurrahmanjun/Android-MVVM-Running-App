package com.abdurrahmanjun.runingapp.ui.statistic

import com.abdurrahmanjun.runingapp.data.local.entity.RunEntity
import com.abdurrahmanjun.runingapp.utils.UnitFormatter
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

enum class StatsPeriod { WEEK, MONTH }

data class StatCard(val label: String, val value: String, val unit: String)

data class WeeklyBar(val dayLabel: String, val fraction: Float, val isPeak: Boolean)

data class PersonalBest(val title: String, val detail: String)

data class StatsUiState(
    val cards: List<StatCard>,
    val bars: List<WeeklyBar>,
    val weekTotalLabel: String,
    val hasChartData: Boolean,
    val personalBest: PersonalBest?,
)

object StatsMapper {

    private fun periodStart(period: StatsPeriod, now: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        when (period) {
            StatsPeriod.WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                if (cal.timeInMillis > now) cal.add(Calendar.DAY_OF_YEAR, -7)
            }
            StatsPeriod.MONTH -> cal.set(Calendar.DAY_OF_MONTH, 1)
        }
        return cal.timeInMillis
    }

    /** Time as H:MM (e.g. 6:12). */
    private fun formatHoursMinutes(ms: Long): String {
        val h = TimeUnit.MILLISECONDS.toHours(ms)
        val m = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        return String.format("%d:%02d", h, m)
    }

    fun build(runs: List<RunEntity>, fmt: UnitFormatter, period: StatsPeriod, now: Long): StatsUiState {
        val start = periodStart(period, now)
        val inPeriod = runs.filter { it.timestamp >= start }

        val totalTime = inPeriod.sumOf { it.timeInMillis }
        val totalMeters = inPeriod.sumOf { it.distanceInMeters }
        val totalCalories = inPeriod.sumOf { it.caloriesBurned }
        val avgSpeed = if (inPeriod.isNotEmpty()) inPeriod.map { it.avgSpeedInKMH }.average().toFloat() else 0f

        val cards = listOf(
            StatCard("Total time", formatHoursMinutes(totalTime), "h"),
            StatCard("Distance", fmt.distanceValue(totalMeters), fmt.distanceUnit),
            StatCard("Calories", "%,d".format(totalCalories), "kcal"),
            StatCard("Avg speed", fmt.speedValue(avgSpeed), fmt.speedUnit),
        )

        // Weekly bar chart is always Mon..Sun of the current week.
        val bars = buildWeeklyBars(runs, fmt, now)
        val weekMeters = currentWeekMeters(runs, now)

        return StatsUiState(
            cards = cards,
            bars = bars.first,
            weekTotalLabel = "${fmt.distanceValue(weekMeters)} ${fmt.distanceUnit}",
            hasChartData = inPeriod.isNotEmpty(),
            personalBest = buildPersonalBest(runs, fmt, now),
        )
    }

    private fun currentWeekMeters(runs: List<RunEntity>, now: Long): Int {
        val start = periodStart(StatsPeriod.WEEK, now)
        return runs.filter { it.timestamp >= start }.sumOf { it.distanceInMeters }
    }

    /** Returns (bars, maxMeters). Bars are Mon..Sun; peak day highlighted. */
    private fun buildWeeklyBars(runs: List<RunEntity>, fmt: UnitFormatter, now: Long): Pair<List<WeeklyBar>, Int> {
        val start = periodStart(StatsPeriod.WEEK, now)
        val labels = listOf("M", "T", "W", "T", "F", "S", "S")
        val perDay = IntArray(7)
        runs.filter { it.timestamp >= start }.forEach { run ->
            val cal = Calendar.getInstance().apply { timeInMillis = run.timestamp }
            // Monday=0 .. Sunday=6
            val idx = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
            perDay[idx] += run.distanceInMeters
        }
        val max = perDay.maxOrNull() ?: 0
        val peakIdx = if (max > 0) perDay.indexOfFirst { it == max } else -1
        val bars = labels.mapIndexed { i, label ->
            WeeklyBar(
                dayLabel = label,
                fraction = if (max > 0) perDay[i].toFloat() / max else 0f,
                isPeak = i == peakIdx && perDay[i] > 0,
            )
        }
        return bars to max
    }

    /** Personal best derived as the longest single run (no schema change). */
    private fun buildPersonalBest(runs: List<RunEntity>, fmt: UnitFormatter, now: Long): PersonalBest? {
        val best = runs.maxByOrNull { it.distanceInMeters } ?: return null
        if (best.distanceInMeters <= 0) return null
        val daysAgo = ((now - best.timestamp) / (24L * 60 * 60 * 1000)).toInt()
        val ago = when (daysAgo) {
            0 -> "today"
            1 -> "yesterday"
            else -> "$daysAgo days ago"
        }
        return PersonalBest(
            title = "Personal best · Longest run",
            detail = "${fmt.distance(best.distanceInMeters)} · ${fmt.clock(best.timeInMillis)} — set $ago",
        )
    }
}
