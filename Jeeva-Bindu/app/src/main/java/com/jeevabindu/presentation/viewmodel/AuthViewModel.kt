package com.jeevabindu.presentation.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeevabindu.domain.model.AuthState
import com.jeevabindu.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val phone: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class OtpUiState(
    val otp: String = "",
    val verificationId: String = "",
    val phoneDisplay: String = "",
    val isLoading: Boolean = false,
    val isResending: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AuthState.Loading)

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _otpUiState = MutableStateFlow(OtpUiState())
    val otpUiState: StateFlow<OtpUiState> = _otpUiState.asStateFlow()

    fun onPhoneChange(phone: String) {
        val digits = phone.filter { it.isDigit() }.take(10)
        _loginUiState.update { it.copy(phone = digits, error = null) }
    }

    fun sendOtp(activity: Activity, onOtpSent: (verificationId: String, phone: String) -> Unit, onAutoVerified: () -> Unit) {
        val phone = _loginUiState.value.phone
        if (phone.length != 10) {
            _loginUiState.update { it.copy(error = "Enter a valid 10-digit mobile number") }
            return
        }
        viewModelScope.launch {
            _loginUiState.update { it.copy(isLoading = true, error = null) }
            authRepository.sendOtp(activity, phone)
                .onSuccess { result ->
                    _loginUiState.update { it.copy(isLoading = false) }
                    if (result.autoVerified) {
                        onAutoVerified()
                    } else {
                        _otpUiState.update {
                            it.copy(
                                verificationId = result.verificationId,
                                phoneDisplay = "+91 $phone",
                                otp = "",
                                error = null
                            )
                        }
                        onOtpSent(result.verificationId, phone)
                    }
                }
                .onFailure { e ->
                    _loginUiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Failed to send OTP")
                    }
                }
        }
    }

    fun initOtpScreen(verificationId: String, phone: String) {
        _otpUiState.update {
            it.copy(
                verificationId = verificationId,
                phoneDisplay = "+91 $phone",
                otp = "",
                error = null,
                successMessage = null
            )
        }
    }

    fun onOtpChange(otp: String) {
        val digits = otp.filter { it.isDigit() }.take(6)
        _otpUiState.update { it.copy(otp = digits, error = null) }
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        val state = _otpUiState.value
        if (state.otp.length != 6) {
            _otpUiState.update { it.copy(error = "Enter the 6-digit OTP") }
            return
        }
        viewModelScope.launch {
            _otpUiState.update { it.copy(isLoading = true, error = null) }
            authRepository.verifyOtp(state.verificationId, state.otp)
                .onSuccess {
                    _otpUiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure { e ->
                    _otpUiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Invalid OTP")
                    }
                }
        }
    }

    fun resendOtp(activity: Activity) {
        viewModelScope.launch {
            _otpUiState.update { it.copy(isResending = true, error = null, successMessage = null) }
            authRepository.resendOtp(activity)
                .onSuccess { result ->
                    _otpUiState.update {
                        it.copy(
                            isResending = false,
                            verificationId = result.verificationId,
                            successMessage = "OTP resent successfully",
                            otp = ""
                        )
                    }
                }
                .onFailure { e ->
                    _otpUiState.update {
                        it.copy(
                            isResending = false,
                            error = e.message ?: "Failed to resend OTP"
                        )
                    }
                }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _loginUiState.value = LoginUiState()
            _otpUiState.value = OtpUiState()
            onLoggedOut()
        }
    }
}
