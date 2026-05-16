package com.jeevabindu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_donors")
data class DonorEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val age: Int,
    val gender: String,
    val bloodGroup: String,
    val phone: String,
    val address: String,
    val district: String,
    val lastDonationDateMillis: Long,
    val availability: Boolean,
    val profileImageUrl: String,
    val lat: Double,
    val lng: Double,
    val isSuspended: Boolean,
    val cachedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_alerts")
data class AlertEntity(
    @PrimaryKey val id: String,
    val requesterId: String,
    val requesterName: String,
    val bloodGroup: String,
    val hospitalName: String,
    val patientCondition: String,
    val unitsRequired: Int,
    val contactNumber: String,
    val locationText: String,
    val district: String,
    val lat: Double,
    val lng: Double,
    val createdAtMillis: Long,
    val active: Boolean,
    val status: String,
    val acceptedCount: Int,
    val cachedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val body: String,
    val type: String,
    val relatedId: String,
    val read: Boolean,
    val createdAtMillis: Long
)
