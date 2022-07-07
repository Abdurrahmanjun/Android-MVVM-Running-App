package com.abdurrahmanjun.runingapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abdurrahmanjun.runingapp.data.local.dao.RunDAO
import com.abdurrahmanjun.runingapp.data.local.entity.RunEntity
import com.abdurrahmanjun.runingapp.data.local.utils.Converters

@Database(
    entities = [RunEntity::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getRunDAO(): RunDAO
}