package com.abdurrahmanjun.runingapp.di

import android.content.Context
import androidx.room.Room
import com.abdurrahmanjun.runingapp.db.RunningDatabase
import com.abdurrahmanjun.runingapp.others.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app, RunningDatabase::class.java,RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDAO(db: RunningDatabase) = db.getRunDAO()
}