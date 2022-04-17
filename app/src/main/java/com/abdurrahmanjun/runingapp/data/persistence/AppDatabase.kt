package com.abdurrahmanjun.runingapp.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abdurrahmanjun.runingapp.data.persistence.models.Run

@Database(
    entities = [Run::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getRunDAO(): RunDAO
}