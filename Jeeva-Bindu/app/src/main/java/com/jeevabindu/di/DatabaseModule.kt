package com.jeevabindu.di

import android.content.Context
import androidx.room.Room
import com.jeevabindu.data.local.AppDatabase
import com.jeevabindu.data.local.dao.AlertDao
import com.jeevabindu.data.local.dao.DonorDao
import com.jeevabindu.data.local.dao.NotificationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "jeeva_bindu.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideDonorDao(db: AppDatabase): DonorDao = db.donorDao()
    @Provides fun provideAlertDao(db: AppDatabase): AlertDao = db.alertDao()
    @Provides fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()
}
