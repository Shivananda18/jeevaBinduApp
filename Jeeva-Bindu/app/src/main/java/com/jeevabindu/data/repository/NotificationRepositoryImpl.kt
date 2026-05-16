package com.jeevabindu.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.jeevabindu.data.local.dao.NotificationDao
import com.jeevabindu.data.local.toEntity
import com.jeevabindu.data.remote.FirestoreConstants
import com.jeevabindu.data.remote.toMap
import com.jeevabindu.data.remote.toNotification
import com.jeevabindu.domain.model.AppNotification
import com.jeevabindu.domain.repository.NotificationRepository
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val notificationDao: NotificationDao,
    @Named("application") private val appScope: CoroutineScope
) : NotificationRepository {

    private val notificationsRef get() = firestore.collection(FirestoreConstants.NOTIFICATIONS)

    override fun observeNotifications(userId: String): Flow<List<AppNotification>> = callbackFlow {
        val listener = notificationsRef
            .whereEqualTo("userId", userId)
            .orderBy("createdAtMillis", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, _ ->
                val items = snapshot?.documents?.mapNotNull { it.toNotification() }.orEmpty()
                if (items.isNotEmpty()) {
                    appScope.launch { notificationDao.insertAll(items.map { it.toEntity() }) }
                }
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveNotification(notification: AppNotification): Result<Unit> = runCatching {
        val doc = if (notification.id.isBlank()) notificationsRef.document()
        else notificationsRef.document(notification.id)
        val withId = notification.copy(id = doc.id)
        doc.set(withId.toMap()).await()
        notificationDao.insertAll(listOf(withId.toEntity()))
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> = runCatching {
        notificationsRef.document(notificationId).update("read", true).await()
        notificationDao.markRead(notificationId)
    }

    override suspend fun markAllRead(userId: String): Result<Unit> = runCatching {
        val snapshot = notificationsRef
            .whereEqualTo("userId", userId)
            .whereEqualTo("read", false)
            .get()
            .await()
        val batch = firestore.batch()
        snapshot.documents.forEach { batch.update(it.reference, "read", true) }
        batch.commit().await()
        notificationDao.markAllRead(userId)
    }
}
