package com.jeevabindu.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.jeevabindu.data.local.dao.DonorDao
import com.jeevabindu.data.local.toEntity
import com.jeevabindu.data.local.toUser
import com.jeevabindu.data.remote.FirestoreConstants
import com.jeevabindu.data.remote.toDonorMap
import com.jeevabindu.data.remote.toUser
import com.jeevabindu.domain.model.User
import com.jeevabindu.domain.repository.DonorRepository
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
class DonorRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val donorDao: DonorDao,
    @Named("application") private val appScope: CoroutineScope
) : DonorRepository {

    private val donorsRef get() = firestore.collection(FirestoreConstants.DONORS)

    override fun observeCurrentDonor(): Flow<User?> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = donorsRef.document(uid).addSnapshotListener { snapshot, _ ->
            val user = snapshot?.toUser()
            if (user != null) {
                appScope.launch { donorDao.insertAll(listOf(user.toEntity())) }
            }
            trySend(user)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getDonor(uid: String): User? =
        runCatching { donorsRef.document(uid).get().await().toUser() }.getOrNull()

    override suspend fun register(user: User): Result<Unit> = runCatching {
        donorsRef.document(user.uid).set(user.toDonorMap()).await()
        donorDao.insertAll(listOf(user.toEntity()))
    }

    override suspend fun updateDonor(user: User): Result<Unit> = runCatching {
        donorsRef.document(user.uid).set(user.toDonorMap()).await()
        donorDao.insertAll(listOf(user.toEntity()))
    }

    override suspend fun uploadProfileImage(uid: String, imageUri: Uri): Result<String> = runCatching {
        val ref = storage.reference.child("profile_images/$uid.jpg")
        ref.putFile(imageUri).await()
        ref.downloadUrl.await().toString()
    }

    override suspend fun updateAvailability(available: Boolean): Result<Unit> = runCatching {
        val uid = firebaseAuth.currentUser?.uid ?: throw IllegalStateException("Not signed in")
        donorsRef.document(uid)
            .update(FirestoreConstants.DonorFields.AVAILABILITY, available)
            .await()
    }

    override suspend fun updateFcmToken(token: String): Result<Unit> = runCatching {
        val uid = firebaseAuth.currentUser?.uid ?: return Result.success(Unit)
        donorsRef.document(uid)
            .update(FirestoreConstants.DonorFields.FCM_TOKEN, token)
            .await()
    }

    override fun observeNearbyDonors(bloodGroup: String, district: String?): Flow<List<User>> =
        searchDonors(
            bloodGroup = bloodGroup.ifBlank { null },
            district = district,
            availableOnly = true
        )

    override fun searchDonors(
        bloodGroup: String?,
        district: String?,
        availableOnly: Boolean
    ): Flow<List<User>> = callbackFlow {
        var query: Query = donorsRef.whereEqualTo(FirestoreConstants.DonorFields.SUSPENDED, false)
        if (!bloodGroup.isNullOrBlank()) {
            query = query.whereEqualTo(FirestoreConstants.DonorFields.BLOOD_GROUP, bloodGroup)
        }
        if (!district.isNullOrBlank() && district != "All") {
            query = query.whereEqualTo(FirestoreConstants.DonorFields.DISTRICT, district)
        }
        if (availableOnly) {
            query = query.whereEqualTo(FirestoreConstants.DonorFields.AVAILABILITY, true)
        }
        val currentUid = firebaseAuth.currentUser?.uid
        val listener = query.limit(50).addSnapshotListener { snapshot, _ ->
            val users = snapshot?.documents?.mapNotNull { it.toUser() }.orEmpty()
                .filter { it.uid != currentUid }
            if (users.isNotEmpty()) {
                appScope.launch { donorDao.insertAll(users.map { it.toEntity() }) }
            }
            trySend(users)
        }
        awaitClose { listener.remove() }
    }
}
