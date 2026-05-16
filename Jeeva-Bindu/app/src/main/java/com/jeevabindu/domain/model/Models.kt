package com.jeevabindu.domain.model

data class User(
    val uid: String = "",
    val name: String = "",
    val age: Int = 18,
    val gender: String = "",
    val bloodGroup: String = "",
    val phone: String = "",
    val address: String = "",
    val district: String = "",
    val lastDonationDateMillis: Long = 0L,
    val availability: Boolean = true,
    val profileImageUrl: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val fcmToken: String = "",
    val isSuspended: Boolean = false,
    val isAdmin: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class EmergencyAlert(
    val id: String = "",
    val requesterId: String = "",
    val requesterName: String = "",
    val bloodGroup: String = "",
    val hospitalName: String = "",
    val patientCondition: String = "",
    val unitsRequired: Int = 1,
    val contactNumber: String = "",
    val locationText: String = "",
    val district: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val active: Boolean = true,
    val status: String = STATUS_ACTIVE,
    val acceptedCount: Int = 0
) {
    companion object {
        const val STATUS_ACTIVE = "active"
        const val STATUS_FULFILLED = "fulfilled"
        const val STATUS_CANCELLED = "cancelled"
    }
}

data class AlertResponse(
    val donorId: String = "",
    val donorName: String = "",
    val donorPhone: String = "",
    val status: String = "",
    val respondedAtMillis: Long = System.currentTimeMillis()
) {
    companion object {
        const val ACCEPTED = "accepted"
        const val REJECTED = "rejected"
    }
}

data class AppNotification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "",
    val relatedId: String = "",
    val read: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class AdminStats(
    val totalDonors: Int = 0,
    val activeEmergencies: Int = 0,
    val availableDonors: Int = 0,
    val suspendedAccounts: Int = 0
)

val BLOOD_GROUPS = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
val GENDERS = listOf("Male", "Female", "Other")
val KARNATAKA_DISTRICTS = listOf(
    "Bagalkot", "Ballari", "Belagavi", "Bengaluru Rural", "Bengaluru Urban",
    "Bidar", "Chamarajanagar", "Chikkaballapur", "Chikkamagaluru", "Chitradurga",
    "Dakshina Kannada", "Davanagere", "Dharwad", "Gadag", "Hassan", "Haveri",
    "Kalaburagi", "Kodagu", "Kolar", "Koppal", "Mandya", "Mysuru", "Raichur",
    "Ramanagara", "Shivamogga", "Tumakuru", "Udupi", "Uttara Kannada",
    "Vijayanagara", "Yadgir", "Other"
)
