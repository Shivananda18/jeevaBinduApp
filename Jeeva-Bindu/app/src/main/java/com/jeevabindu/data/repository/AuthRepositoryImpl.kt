package com.jeevabindu.data.repository

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.jeevabindu.data.local.AuthPreferences
import com.jeevabindu.domain.model.AuthState
import com.jeevabindu.domain.model.OtpSendResult
import com.jeevabindu.domain.repository.AuthRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authPreferences: AuthPreferences,
    @javax.inject.Named("application") private val appScope: CoroutineScope
) : AuthRepository {

    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var lastPhoneE164: String? = null

    override val authState: Flow<AuthState> = callbackFlow {
        trySend(AuthState.Loading)
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                appScope.launch {
                    authPreferences.saveSession(
                        uid = user.uid,
                        phone = user.phoneNumber.orEmpty()
                    )
                }
            }
            trySend(mapFirebaseUser(user))
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override val currentUserId: Flow<String?> = authState.map { state ->
        (state as? AuthState.Authenticated)?.uid
    }

    override suspend fun sendOtp(activity: Activity, phone: String): Result<OtpSendResult> {
        val e164 = formatIndianPhone(phone)
            ?: return Result.failure(IllegalArgumentException("Enter a valid 10-digit mobile number"))

        lastPhoneE164 = e164
        return requestOtp(activity, e164, resendToken = null)
    }

    override suspend fun resendOtp(activity: Activity): Result<OtpSendResult> {
        val phone = lastPhoneE164
            ?: return Result.failure(IllegalStateException("No phone number to resend OTP"))
        val token = resendToken
            ?: return sendOtp(activity, phone.removePrefix("+91"))
        return requestOtp(activity, phone, resendToken = token)
    }

    override suspend fun verifyOtp(verificationId: String, otp: String): Result<String> {
        if (otp.length != 6 || !otp.all { it.isDigit() }) {
            return Result.failure(IllegalArgumentException("Enter a valid 6-digit OTP"))
        }
        return runCatching {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            signInWithCredential(credential, lastPhoneE164.orEmpty())
        }
    }

    override fun currentUid(): String? = firebaseAuth.currentUser?.uid

    override suspend fun logout() {
        firebaseAuth.signOut()
        authPreferences.clearSession()
        resendToken = null
        lastPhoneE164 = null
    }

    private suspend fun requestOtp(
        activity: Activity,
        phoneE164: String,
        resendToken: PhoneAuthProvider.ForceResendingToken?
    ): Result<OtpSendResult> = suspendCancellableCoroutine { continuation ->
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if (!continuation.isActive) return
                firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        if (continuation.isActive) {
                            continuation.resume(
                                Result.success(
                                    OtpSendResult(verificationId = "", autoVerified = true)
                                )
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        if (continuation.isActive) {
                            continuation.resume(Result.failure(e))
                        }
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (continuation.isActive) {
                    continuation.resume(Result.failure(e))
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                this@AuthRepositoryImpl.resendToken = token
                if (continuation.isActive) {
                    continuation.resume(
                        Result.success(OtpSendResult(verificationId = verificationId))
                    )
                }
            }
        }

        val builder = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneE164)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        if (resendToken != null) {
            builder.setForceResendingToken(resendToken)
        }

        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }

    private suspend fun signInWithCredential(
        credential: PhoneAuthCredential,
        phoneE164: String
    ): String {
        val result = firebaseAuth.signInWithCredential(credential).await()
        val user = result.user ?: throw IllegalStateException("Sign-in succeeded but user is null")
        val phone = user.phoneNumber ?: phoneE164
        authPreferences.saveSession(user.uid, phone)
        return user.uid
    }

    private fun mapFirebaseUser(user: com.google.firebase.auth.FirebaseUser?): AuthState {
        return if (user != null) {
            AuthState.Authenticated(uid = user.uid, phone = user.phoneNumber)
        } else {
            AuthState.Unauthenticated
        }
    }

    private fun formatIndianPhone(input: String): String? {
        val digits = input.filter { it.isDigit() }
        val normalized = when {
            digits.length == 10 && digits.first() in '6'..'9' -> digits
            digits.length == 12 && digits.startsWith("91") -> digits.drop(2)
            else -> return null
        }
        return "+91$normalized"
    }
}
