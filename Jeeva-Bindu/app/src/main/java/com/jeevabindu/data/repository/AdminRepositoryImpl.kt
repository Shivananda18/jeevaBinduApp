package com.jeevabindu.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.jeevabindu.data.remote.FirestoreConstants
import com.jeevabindu.data.remote.toEmergencyAlert
import com.jeevabindu.data.remote.toUser
import com.jeevabindu.domain.model.AdminStats
import com.jeevabindu.domain.model.EmergencyAlert
import com.jeevabindu.domain.model.User
import com.jeevabindu.domain.repository.AdminRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminRepository {

    override suspend fun verifyAdmin(uid: String): Boolean = runCatching {
        firestore.collection(FirestoreConstants.ADMINS).document(uid).get().await().exists()
    }.getOrDefault(false)

    override fun observeStats(): Flow<AdminStats> = callbackFlow {
        val donorsListener = firestore.collection(FirestoreConstants.DONORS)
            .addSnapshotListener { donorSnap, _ ->
                val donors = donorSnap?.documents?.mapNotNull { it.toUser() }.orEmpty()
                firestore.collection(FirestoreConstants.EMERGENCIES)
                    .whereEqualTo(FirestoreConstants.EmergencyFields.ACTIVE, true)
                    .get()
                    .addOnSuccessListener { emergencySnap ->
                        val emergencies = emergencySnap.documents.map { it.toEmergencyAlert() }
                        trySend(
                            AdminStats(
                                totalDonors = donors.size,
                                activeEmergencies = emergencies.size,
                                availableDonors = donors.count { it.availability && !it.isSuspended },
                                suspendedAccounts = donors.count { it.isSuspended }
                            )
                        )
                    }
            }
        awaitClose { donorsListener.remove() }
    }

    override suspend fun suspendUser(uid: String, suspend: Boolean): Result<Unit> = runCatching {
        firestore.collection(FirestoreConstants.DONORS).document(uid)
            .update(FirestoreConstants.DonorFields.SUSPENDED, suspend)
            .await()
    }

    override fun observeAllDonors(): Flow<List<User>> = callbackFlow {
        val listener = firestore.collection(FirestoreConstants.DONORS)
            .orderBy(FirestoreConstants.DonorFields.NAME)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.documents?.mapNotNull { it.toUser() }.orEmpty())
            }
        awaitClose { listener.remove() }
    }

    override fun observeAllEmergencies(): Flow<List<EmergencyAlert>> = callbackFlow {
        val listener = firestore.collection(FirestoreConstants.EMERGENCIES)
            .orderBy(FirestoreConstants.EmergencyFields.CREATED_AT, com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.documents?.map { it.toEmergencyAlert() }.orEmpty())
            }
        awaitClose { listener.remove() }
    }
}
