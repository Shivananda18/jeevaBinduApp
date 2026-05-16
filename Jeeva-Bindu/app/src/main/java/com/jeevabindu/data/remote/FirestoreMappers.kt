package com.jeevabindu.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.jeevabindu.domain.model.AppNotification
import com.jeevabindu.domain.model.EmergencyAlert
import com.jeevabindu.domain.model.User

fun DocumentSnapshot.toUser(): User? {
    if (!exists()) return null
    return User(
        uid = getString(FirestoreConstants.DonorFields.UID) ?: id,
        name = getString(FirestoreConstants.DonorFields.NAME).orEmpty(),
        age = getLong(FirestoreConstants.DonorFields.AGE)?.toInt() ?: 18,
        gender = getString(FirestoreConstants.DonorFields.GENDER).orEmpty(),
        bloodGroup = getString(FirestoreConstants.DonorFields.BLOOD_GROUP).orEmpty(),
        phone = getString(FirestoreConstants.DonorFields.PHONE).orEmpty(),
        address = getString(FirestoreConstants.DonorFields.ADDRESS).orEmpty(),
        district = getString(FirestoreConstants.DonorFields.DISTRICT).orEmpty(),
        lastDonationDateMillis = getLong(FirestoreConstants.DonorFields.LAST_DONATION) ?: 0L,
        availability = getBoolean(FirestoreConstants.DonorFields.AVAILABILITY) ?: true,
        profileImageUrl = getString(FirestoreConstants.DonorFields.PROFILE_IMAGE).orEmpty(),
        lat = getDouble(FirestoreConstants.DonorFields.LAT) ?: 0.0,
        lng = getDouble(FirestoreConstants.DonorFields.LNG) ?: 0.0,
        fcmToken = getString(FirestoreConstants.DonorFields.FCM_TOKEN).orEmpty(),
        isSuspended = getBoolean(FirestoreConstants.DonorFields.SUSPENDED) ?: false,
        isAdmin = getBoolean(FirestoreConstants.DonorFields.IS_ADMIN) ?: false,
        createdAtMillis = getLong(FirestoreConstants.DonorFields.CREATED_AT) ?: 0L
    )
}

fun User.toDonorMap(): Map<String, Any?> = mapOf(
    FirestoreConstants.DonorFields.UID to uid,
    FirestoreConstants.DonorFields.NAME to name,
    FirestoreConstants.DonorFields.AGE to age,
    FirestoreConstants.DonorFields.GENDER to gender,
    FirestoreConstants.DonorFields.BLOOD_GROUP to bloodGroup,
    FirestoreConstants.DonorFields.PHONE to phone,
    FirestoreConstants.DonorFields.ADDRESS to address,
    FirestoreConstants.DonorFields.DISTRICT to district,
    FirestoreConstants.DonorFields.LAST_DONATION to lastDonationDateMillis,
    FirestoreConstants.DonorFields.AVAILABILITY to availability,
    FirestoreConstants.DonorFields.PROFILE_IMAGE to profileImageUrl,
    FirestoreConstants.DonorFields.LAT to lat,
    FirestoreConstants.DonorFields.LNG to lng,
    FirestoreConstants.DonorFields.FCM_TOKEN to fcmToken,
    FirestoreConstants.DonorFields.SUSPENDED to isSuspended,
    FirestoreConstants.DonorFields.IS_ADMIN to isAdmin,
    FirestoreConstants.DonorFields.CREATED_AT to createdAtMillis
)

fun DocumentSnapshot.toEmergencyAlert(): EmergencyAlert = EmergencyAlert(
    id = id,
    requesterId = getString(FirestoreConstants.EmergencyFields.REQUESTER_ID).orEmpty(),
    requesterName = getString(FirestoreConstants.EmergencyFields.REQUESTER_NAME).orEmpty(),
    bloodGroup = getString(FirestoreConstants.EmergencyFields.BLOOD_GROUP).orEmpty(),
    hospitalName = getString(FirestoreConstants.EmergencyFields.HOSPITAL).orEmpty(),
    patientCondition = getString(FirestoreConstants.EmergencyFields.CONDITION).orEmpty(),
    unitsRequired = getLong(FirestoreConstants.EmergencyFields.UNITS)?.toInt() ?: 1,
    contactNumber = getString(FirestoreConstants.EmergencyFields.CONTACT).orEmpty(),
    locationText = getString(FirestoreConstants.EmergencyFields.LOCATION).orEmpty(),
    district = getString(FirestoreConstants.EmergencyFields.DISTRICT).orEmpty(),
    lat = getDouble(FirestoreConstants.EmergencyFields.LAT) ?: 0.0,
    lng = getDouble(FirestoreConstants.EmergencyFields.LNG) ?: 0.0,
    createdAtMillis = getLong(FirestoreConstants.EmergencyFields.CREATED_AT) ?: 0L,
    active = getBoolean(FirestoreConstants.EmergencyFields.ACTIVE) ?: true,
    status = getString(FirestoreConstants.EmergencyFields.STATUS) ?: EmergencyAlert.STATUS_ACTIVE,
    acceptedCount = getLong(FirestoreConstants.EmergencyFields.ACCEPTED_COUNT)?.toInt() ?: 0
)

fun EmergencyAlert.toEmergencyMap(): Map<String, Any?> = mapOf(
    FirestoreConstants.EmergencyFields.REQUESTER_ID to requesterId,
    FirestoreConstants.EmergencyFields.REQUESTER_NAME to requesterName,
    FirestoreConstants.EmergencyFields.BLOOD_GROUP to bloodGroup,
    FirestoreConstants.EmergencyFields.HOSPITAL to hospitalName,
    FirestoreConstants.EmergencyFields.CONDITION to patientCondition,
    FirestoreConstants.EmergencyFields.UNITS to unitsRequired,
    FirestoreConstants.EmergencyFields.CONTACT to contactNumber,
    FirestoreConstants.EmergencyFields.LOCATION to locationText,
    FirestoreConstants.EmergencyFields.DISTRICT to district,
    FirestoreConstants.EmergencyFields.LAT to lat,
    FirestoreConstants.EmergencyFields.LNG to lng,
    FirestoreConstants.EmergencyFields.CREATED_AT to createdAtMillis,
    FirestoreConstants.EmergencyFields.ACTIVE to active,
    FirestoreConstants.EmergencyFields.STATUS to status,
    FirestoreConstants.EmergencyFields.ACCEPTED_COUNT to acceptedCount
)

fun DocumentSnapshot.toNotification(): AppNotification? {
    if (!exists()) return null
    return AppNotification(
        id = id,
        userId = getString("userId").orEmpty(),
        title = getString("title").orEmpty(),
        body = getString("body").orEmpty(),
        type = getString("type").orEmpty(),
        relatedId = getString("relatedId").orEmpty(),
        read = getBoolean("read") ?: false,
        createdAtMillis = getLong("createdAtMillis") ?: 0L
    )
}

fun AppNotification.toMap(): Map<String, Any?> = mapOf(
    "userId" to userId,
    "title" to title,
    "body" to body,
    "type" to type,
    "relatedId" to relatedId,
    "read" to read,
    "createdAtMillis" to createdAtMillis
)
