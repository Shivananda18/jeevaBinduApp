package com.jeevabindu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.jeevabindu.data.local.dao.AlertDao
import com.jeevabindu.data.local.toAlert
import com.jeevabindu.data.local.toEntity
import com.jeevabindu.data.remote.FirestoreConstants
import com.jeevabindu.data.remote.toEmergencyAlert
import com.jeevabindu.data.remote.toEmergencyMap
import com.jeevabindu.domain.model.AlertResponse
import com.jeevabindu.domain.model.EmergencyAlert
import com.jeevabindu.domain.repository.AlertRepository
import com.jeevabindu.domain.repository.DonorRepository
import dagger.Lazy
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
class AlertRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val alertDao: AlertDao,
    private val donorRepository: Lazy<DonorRepository>,
    @Named("application") private val appScope: CoroutineScope
) : AlertRepository {

    private val emergenciesRef get() = firestore.collection(FirestoreConstants.EMERGENCIES)

    override suspend fun createAlert(alert: EmergencyAlert): Result<String> = runCatching {
        val doc = emergenciesRef.document()
        val data = alert.copy(id = doc.id).toEmergencyMap()
        doc.set(data).await()
        doc.id
    }

    override fun observeAlerts(): Flow<List<EmergencyAlert>> = callbackFlow {
        val listener = emergenciesRef
            .whereEqualTo(FirestoreConstants.EmergencyFields.ACTIVE, true)
            .orderBy(FirestoreConstants.EmergencyFields.CREATED_AT, com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(30)
            .addSnapshotListener { snapshot, _ ->
                val alerts = snapshot?.documents?.map { it.toEmergencyAlert() }.orEmpty()
                if (alerts.isNotEmpty()) {
                    appScope.launch { alertDao.insertAll(alerts.map { it.toEntity() }) }
                }
                trySend(alerts)
            }
        awaitClose { listener.remove() }
    }

    override fun observeAlertsForBloodGroup(bloodGroup: String): Flow<List<EmergencyAlert>> =
        callbackFlow {
            val listener = emergenciesRef
                .whereEqualTo(FirestoreConstants.EmergencyFields.ACTIVE, true)
                .whereEqualTo(FirestoreConstants.EmergencyFields.BLOOD_GROUP, bloodGroup)
                .orderBy(FirestoreConstants.EmergencyFields.CREATED_AT, com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(20)
                .addSnapshotListener { snapshot, _ ->
                    trySend(snapshot?.documents?.map { it.toEmergencyAlert() }.orEmpty())
                }
            awaitClose { listener.remove() }
        }

    override suspend fun respondToAlert(alertId: String, accept: Boolean): Result<Unit> = runCatching {
        val uid = firebaseAuth.currentUser?.uid ?: throw IllegalStateException("Not signed in")
        val donor = donorRepository.get().getDonor(uid) ?: throw IllegalStateException("Donor profile not found")
        val responseRef = emergenciesRef.document(alertId)
            .collection(FirestoreConstants.RESPONSES)
            .document(uid)
        val response = mapOf(
            "donorId" to uid,
            "donorName" to donor.name,
            "donorPhone" to donor.phone,
            "status" to if (accept) AlertResponse.ACCEPTED else AlertResponse.REJECTED,
            "respondedAtMillis" to System.currentTimeMillis()
        )
        responseRef.set(response).await()
        if (accept) {
            emergenciesRef.document(alertId)
                .update(
                    FirestoreConstants.EmergencyFields.ACCEPTED_COUNT,
                    FieldValue.increment(1)
                )
                .await()
        }
    }

    override suspend fun cancelAlert(alertId: String): Result<Unit> = runCatching {
        emergenciesRef.document(alertId).update(
            mapOf(
                FirestoreConstants.EmergencyFields.ACTIVE to false,
                FirestoreConstants.EmergencyFields.STATUS to EmergencyAlert.STATUS_CANCELLED
            )
        ).await()
    }
}
