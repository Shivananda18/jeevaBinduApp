package com.jeevabindu.data.remote

object FirestoreConstants {
    const val DONORS = "donors"
    const val EMERGENCIES = "emergencies"
    const val RESPONSES = "responses"
    const val NOTIFICATIONS = "notifications"
    const val ADMINS = "admins"

    object DonorFields {
        const val UID = "uid"
        const val NAME = "name"
        const val AGE = "age"
        const val GENDER = "gender"
        const val BLOOD_GROUP = "bloodGroup"
        const val PHONE = "phone"
        const val ADDRESS = "address"
        const val DISTRICT = "district"
        const val LAST_DONATION = "lastDonationDateMillis"
        const val AVAILABILITY = "availability"
        const val PROFILE_IMAGE = "profileImageUrl"
        const val LAT = "lat"
        const val LNG = "lng"
        const val FCM_TOKEN = "fcmToken"
        const val SUSPENDED = "isSuspended"
        const val IS_ADMIN = "isAdmin"
        const val CREATED_AT = "createdAtMillis"
    }

    object EmergencyFields {
        const val REQUESTER_ID = "requesterId"
        const val REQUESTER_NAME = "requesterName"
        const val BLOOD_GROUP = "bloodGroup"
        const val HOSPITAL = "hospitalName"
        const val CONDITION = "patientCondition"
        const val UNITS = "unitsRequired"
        const val CONTACT = "contactNumber"
        const val LOCATION = "locationText"
        const val DISTRICT = "district"
        const val LAT = "lat"
        const val LNG = "lng"
        const val CREATED_AT = "createdAtMillis"
        const val ACTIVE = "active"
        const val STATUS = "status"
        const val ACCEPTED_COUNT = "acceptedCount"
    }
}
