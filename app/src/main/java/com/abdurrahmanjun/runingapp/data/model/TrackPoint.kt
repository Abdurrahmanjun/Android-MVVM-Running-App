package com.abdurrahmanjun.runingapp.data.model

/**
 * A single GPS fix with its wall-clock timestamp. The persisted run keeps a
 * list of these so the ETA work later can replay a run's real pace timeline.
 */
data class TrackPoint(
    val lat: Double,
    val lng: Double,
    val timeMs: Long
)
