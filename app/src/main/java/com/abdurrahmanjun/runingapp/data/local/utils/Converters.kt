package com.abdurrahmanjun.runingapp.data.local.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.abdurrahmanjun.runingapp.data.model.TrackPoint
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @TypeConverter
    fun fromBitmap(bmp: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun fromTrace(trace: List<TrackPoint>): String {
        val array = JSONArray()
        trace.forEach { point ->
            array.put(
                JSONObject()
                    .put("lat", point.lat)
                    .put("lng", point.lng)
                    .put("t", point.timeMs)
            )
        }
        return array.toString()
    }

    @TypeConverter
    fun toTrace(json: String): List<TrackPoint> {
        if (json.isBlank()) return emptyList()
        val array = JSONArray(json)
        return List(array.length()) { i ->
            val obj = array.getJSONObject(i)
            TrackPoint(obj.getDouble("lat"), obj.getDouble("lng"), obj.getLong("t"))
        }
    }
}