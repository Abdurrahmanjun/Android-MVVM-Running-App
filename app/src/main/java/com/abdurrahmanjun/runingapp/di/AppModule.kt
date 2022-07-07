package com.abdurrahmanjun.runingapp.di

import android.content.Context
import androidx.room.Room
import com.abdurrahmanjun.runingapp.data.local.AppDatabase
import com.abdurrahmanjun.runingapp.utils.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app, AppDatabase::class.java,RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDAO(db: AppDatabase) = db.getRunDAO()
}