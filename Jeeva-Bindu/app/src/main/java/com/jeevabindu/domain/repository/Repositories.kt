package com.jeevabindu.domain.repository

import android.app.Activity
import android.net.Uri
import com.jeevabindu.domain.model.AdminStats
import com.jeevabindu.domain.model.AppNotification
import com.jeevabindu.domain.model.AuthState
import com.jeevabindu.domain.model.EmergencyAlert
import com.jeevabindu.domain.model.OtpSendResult
import com.jeevabindu.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<AuthState>
    val currentUserId: Flow<String?>
    suspend fun sendOtp(activity: Activity, phone: String): Result<OtpSendResult>
    suspend fun resendOtp(activity: Activity): Result<OtpSendResult>
    suspend fun verifyOtp(verificationId: String, otp: String): Result<String>
    suspend fun logout()
    fun currentUid(): String?
}

interface DonorRepository {
    fun observeCurrentDonor(): Flow<User?>
    suspend fun getDonor(uid: String): User?
    suspend fun register(user: User): Result<Unit>
    suspend fun updateDonor(user: User): Result<Unit>
    suspend fun uploadProfileImage(uid: String, imageUri: Uri): Result<String>
    suspend fun updateAvailability(available: Boolean): Result<Unit>
    suspend fun updateFcmToken(token: String): Result<Unit>
    fun observeNearbyDonors(bloodGroup: String, district: String? = null): Flow<List<User>>
    fun searchDonors(
        bloodGroup: String?,
        district: String?,
        availableOnly: Boolean
    ): Flow<List<User>>
}

interface AlertRepository {
    suspend fun createAlert(alert: EmergencyAlert): Result<String>
    fun observeAlerts(): Flow<List<EmergencyAlert>>
    fun observeAlertsForBloodGroup(bloodGroup: String): Flow<List<EmergencyAlert>>
    suspend fun respondToAlert(alertId: String, accept: Boolean): Result<Unit>
    suspend fun cancelAlert(alertId: String): Result<Unit>
}

interface NotificationRepository {
    fun observeNotifications(userId: String): Flow<List<AppNotification>>
    suspend fun saveNotification(notification: AppNotification): Result<Unit>
    suspend fun markAsRead(notificationId: String): Result<Unit>
    suspend fun markAllRead(userId: String): Result<Unit>
}

interface AdminRepository {
    suspend fun verifyAdmin(uid: String): Boolean
    fun observeStats(): Flow<AdminStats>
    suspend fun suspendUser(uid: String, suspend: Boolean): Result<Unit>
    fun observeAllDonors(): Flow<List<User>>
    fun observeAllEmergencies(): Flow<List<EmergencyAlert>>
}

interface ThemeRepository {
    val isDarkMode: Flow<Boolean?>
    suspend fun setDarkMode(enabled: Boolean)
}
