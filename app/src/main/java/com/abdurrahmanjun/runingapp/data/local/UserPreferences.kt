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

    companion object {
        const val UNITS_METRIC = "metric"
        const val UNITS_IMPERIAL = "imperial"

        private const val PREFS_NAME = "shared_pref"
        private const val KEY_NAME = "KEY_NAME"
        private const val KEY_WEIGHT = "KEY_WEIGHT"
        private const val KEY_FIRST_OPEN = "KEY_FIRST_OPEN"
        private const val KEY_UNITS = "KEY_UNITS"
    }
}
