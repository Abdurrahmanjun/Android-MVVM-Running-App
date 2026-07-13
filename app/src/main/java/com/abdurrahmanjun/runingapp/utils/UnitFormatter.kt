package com.abdurrahmanjun.runingapp.utils

import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/**
 * Formats distance / pace / speed according to the user's chosen units so a single
 * source drives every screen (the design's "units re-format app-wide" requirement).
 */
class UnitFormatter(private val metric: Boolean) {

    private companion object {
        const val METERS_PER_KM = 1000.0
        const val METERS_PER_MILE = 1609.344
    }

    val distanceUnit: String get() = if (metric) "km" else "mi"
    val paceUnit: String get() = if (metric) "/km" else "/mi"
    val speedUnit: String get() = if (metric) "km/h" else "mph"

    private fun metersToMajor(meters: Int): Double =
        meters / (if (metric) METERS_PER_KM else METERS_PER_MILE)

    /** Distance value only, one decimal, e.g. "6.2". */
    fun distanceValue(meters: Int): String = String.format("%.1f", metersToMajor(meters))

    /** Distance with unit, e.g. "6.2 km". */
    fun distance(meters: Int): String = "${distanceValue(meters)} $distanceUnit"

    /** Convert a km value (e.g. weekly goal) into the display unit value string. */
    fun kmValue(km: Float): String =
        String.format("%.1f", if (metric) km.toDouble() else km / (METERS_PER_MILE / METERS_PER_KM))

    /** Pace as M'SS" per major unit; blank when indeterminate. */
    fun pace(meters: Int, timeMillis: Long): String {
        if (meters <= 0 || timeMillis <= 0) return "--'--\""
        val major = metersToMajor(meters)
        val secPerMajor = (timeMillis / 1000.0) / major
        if (secPerMajor.isInfinite() || secPerMajor.isNaN()) return "--'--\""
        val totalSec = secPerMajor.roundToInt()
        val min = totalSec / 60
        val sec = totalSec % 60
        return String.format("%d'%02d\"", min, sec)
    }

    /** Speed from km/h into the display unit, one decimal. */
    fun speedValue(kmh: Float): String =
        String.format("%.1f", if (metric) kmh else kmh / (METERS_PER_MILE / METERS_PER_KM).toFloat())

    /** Elapsed run time as M:SS or H:MM:SS. */
    fun clock(timeMillis: Long): String {
        val h = TimeUnit.MILLISECONDS.toHours(timeMillis)
        val m = TimeUnit.MILLISECONDS.toMinutes(timeMillis) % 60
        val s = TimeUnit.MILLISECONDS.toSeconds(timeMillis) % 60
        return if (h > 0) String.format("%d:%02d:%02d", h, m, s) else String.format("%d:%02d", m, s)
    }
}
