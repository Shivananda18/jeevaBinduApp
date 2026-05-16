package com.jeevabindu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeevabindu.data.local.entity.AlertEntity
import com.jeevabindu.data.local.entity.DonorEntity
import com.jeevabindu.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {
    @Query("SELECT * FROM cached_donors WHERE isSuspended = 0 ORDER BY name ASC")
    fun observeAll(): Flow<List<DonorEntity>>

    @Query("SELECT * FROM cached_donors WHERE uid = :uid LIMIT 1")
    suspend fun getById(uid: String): DonorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(donors: List<DonorEntity>)

    @Query("DELETE FROM cached_donors")
    suspend fun clear()
}

@Dao
interface AlertDao {
    @Query("SELECT * FROM cached_alerts WHERE active = 1 ORDER BY createdAtMillis DESC")
    fun observeActive(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alerts: List<AlertEntity>)

    @Query("DELETE FROM cached_alerts")
    suspend fun clear()
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM cached_notifications WHERE userId = :userId ORDER BY createdAtMillis DESC")
    fun observeForUser(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Query("UPDATE cached_notifications SET read = 1 WHERE id = :id")
    suspend fun markRead(id: String)

    @Query("UPDATE cached_notifications SET read = 1 WHERE userId = :userId")
    suspend fun markAllRead(userId: String)
}
