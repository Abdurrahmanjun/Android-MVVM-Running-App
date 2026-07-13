package com.abdurrahmanjun.runingapp.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple SharedPreferences-backed store for the user's profile (name + weight),
 * used for the welcome greeting and calorie estimation. DataStore migration is
 * deferred to the modernization tier.
 */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var name: String
        get() = prefs.getString(KEY_NAME, "") ?: ""
        set(value) = prefs.edit { putString(KEY_NAME, value) }

    var weightKg: Float
        get() = prefs.getFloat(KEY_WEIGHT, 70f)
        set(value) = prefs.edit { putFloat(KEY_WEIGHT, value) }

    var isFirstAppOpen: Boolean
        get() = prefs.getBoolean(KEY_FIRST_OPEN, true)
        set(value) = prefs.edit { putBoolean(KEY_FIRST_OPEN, value) }

    /** Measurement units: [UNITS_METRIC] (km) or [UNITS_IMPERIAL] (mi). */
    var units: String
        get() = prefs.getString(KEY_UNITS, UNITS_METRIC) ?: UNITS_METRIC
        set(value) = prefs.edit { putString(KEY_UNITS, value) }

    val isMetric: Boolean get() = units == UNITS_METRIC

    /** Weekly distance goal in kilometres (used by Home hero + Stats). */
    var weeklyGoalKm: Float
        get() = prefs.getFloat(KEY_WEEKLY_GOAL, 24f)
        set(value) = prefs.edit { putFloat(KEY_WEEKLY_GOAL, value) }

    var autoPause: Boolean
        get() = prefs.getBoolean(KEY_AUTO_PAUSE, true)
        set(value) = prefs.edit { putBoolean(KEY_AUTO_PAUSE, value) }

    var voiceCoach: Boolean
        get() = prefs.getBoolean(KEY_VOICE_COACH, true)
        set(value) = prefs.edit { putBoolean(KEY_VOICE_COACH, value) }

    var darkMapAtNight: Boolean
        get() = prefs.getBoolean(KEY_DARK_MAP, false)
        set(value) = prefs.edit { putBoolean(KEY_DARK_MAP, value) }

    companion object {
        const val UNITS_METRIC = "metric"
        const val UNITS_IMPERIAL = "imperial"

        private const val PREFS_NAME = "shared_pref"
        private const val KEY_NAME = "KEY_NAME"
        private const val KEY_WEIGHT = "KEY_WEIGHT"
        private const val KEY_FIRST_OPEN = "KEY_FIRST_OPEN"
        private const val KEY_UNITS = "KEY_UNITS"
        private const val KEY_WEEKLY_GOAL = "KEY_WEEKLY_GOAL"
        private const val KEY_AUTO_PAUSE = "KEY_AUTO_PAUSE"
        private const val KEY_VOICE_COACH = "KEY_VOICE_COACH"
        private const val KEY_DARK_MAP = "KEY_DARK_MAP"
    }
}
