package com.jeevabindu.domain.model

sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val uid: String, val phone: String?) : AuthState()
}

data class OtpSendResult(
    val verificationId: String,
    val autoVerified: Boolean = false
)
