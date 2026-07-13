package com.abdurrahmanjun.runingapp.data.local.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abdurrahmanjun.runingapp.data.model.TrackPoint

@Entity(tableName = "running_table")
data class RunEntity(
    var img: Bitmap? = null,
    var timestamp: Long = 0L,
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var timeInMillis: Long = 0L,
    var caloriesBurned: Int = 0,
    // Timestamped GPS trace of the run, for later ETA replay/analysis.
    var trace: List<TrackPoint> = emptyList()
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}