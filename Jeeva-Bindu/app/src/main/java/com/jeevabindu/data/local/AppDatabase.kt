package com.jeevabindu.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jeevabindu.data.local.dao.AlertDao
import com.jeevabindu.data.local.dao.DonorDao
import com.jeevabindu.data.local.dao.NotificationDao
import com.jeevabindu.data.local.entity.AlertEntity
import com.jeevabindu.data.local.entity.DonorEntity
import com.jeevabindu.data.local.entity.NotificationEntity

@Database(
    entities = [DonorEntity::class, AlertEntity::class, NotificationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun donorDao(): DonorDao
    abstract fun alertDao(): AlertDao
    abstract fun notificationDao(): NotificationDao
}
