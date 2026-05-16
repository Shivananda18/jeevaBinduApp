package com.jeevabindu.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeevabindu.domain.model.AdminStats
import com.jeevabindu.domain.model.AppNotification
import com.jeevabindu.domain.model.BLOOD_GROUPS
import com.jeevabindu.domain.model.EmergencyAlert
import com.jeevabindu.domain.model.User
import com.jeevabindu.domain.repository.AdminRepository
import com.jeevabindu.domain.repository.AlertRepository
import com.jeevabindu.domain.repository.AuthRepository
import com.jeevabindu.domain.repository.DonorRepository
import com.jeevabindu.domain.repository.NotificationRepository
import com.jeevabindu.domain.repository.ThemeRepository
import com.jeevabindu.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── Home ──────────────────────────────────────────────────────────────────────

data class HomeUiState(
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val currentDonor: User? = null,
    val nearbyDonors: List<User> = emptyList(),
    val alerts: List<EmergencyAlert> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val donorRepository: DonorRepository,
    private val alertRepository: AlertRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var started = false

    fun start() {
        if (started) return
        started = true
        viewModelScope.launch {
            donorRepository.observeCurrentDonor().collect { donor ->
                _uiState.update { it.copy(currentDonor = donor) }
                if (donor != null) observeDonorsAndAlerts(donor.bloodGroup, donor.district)
            }
        }
    }

    private fun observeDonorsAndAlerts(bloodGroup: String, district: String) {
        viewModelScope.launch {
            donorRepository.observeNearbyDonors(bloodGroup, district).collect { donors ->
                _uiState.update { it.copy(loading = false, refreshing = false, nearbyDonors = donors) }
            }
        }
        viewModelScope.launch {
            alertRepository.observeAlerts().collect { alerts ->
                _uiState.update { it.copy(alerts = alerts) }
            }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(refreshing = true) }
        val donor = _uiState.value.currentDonor ?: return
        observeDonorsAndAlerts(donor.bloodGroup, donor.district)
    }

    fun toggleAvailability() {
        val donor = _uiState.value.currentDonor ?: return
        viewModelScope.launch {
            donorRepository.updateAvailability(!donor.availability)
                .onFailure { e -> _uiState.update { s -> s.copy(error = e.message) } }
        }
    }
}

// ── Registration / Profile ────────────────────────────────────────────────────

data class RegistrationUiState(
    val uid: String = "",
    val name: String = "",
    val age: String = "18",
    val gender: String = "",
    val bloodGroup: String = "",
    val phone: String = "",
    val address: String = "",
    val district: String = "",
    val lastDonationDateMillis: Long = 0L,
    val availability: Boolean = true,
    val profileImageUrl: String = "",
    val pendingImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val isEditMode: Boolean = false
)

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val donorRepository: DonorRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun bootstrap(phone: String = "") {
        viewModelScope.launch {
            val uid = authRepository.currentUid() ?: return@launch
            val donor = donorRepository.getDonor(uid)
            if (donor != null) {
                _uiState.update {
                    it.copy(
                        uid = donor.uid,
                        name = donor.name,
                        age = donor.age.toString(),
                        gender = donor.gender,
                        bloodGroup = donor.bloodGroup,
                        phone = donor.phone,
                        address = donor.address,
                        district = donor.district,
                        lastDonationDateMillis = donor.lastDonationDateMillis,
                        availability = donor.availability,
                        profileImageUrl = donor.profileImageUrl,
                        isEditMode = true
                    )
                }
            } else {
                initRegistration(phone)
            }
        }
    }

    fun initRegistration(phone: String) {
        val uid = authRepository.currentUid().orEmpty()
        val normalizedPhone = phone.ifBlank { "" }.filter { it.isDigit() }.takeLast(10)
        _uiState.update {
            it.copy(uid = uid, phone = normalizedPhone, isEditMode = false)
        }
    }

    fun initFromAuthPhone(firebasePhone: String?) {
        val digits = firebasePhone.orEmpty().filter { it.isDigit() }.takeLast(10)
        initRegistration(digits)
    }

    fun update(field: String, value: String) {
        _uiState.update {
            when (field) {
                "name" -> it.copy(name = value)
                "age" -> it.copy(age = value.filter { c -> c.isDigit() }.take(2))
                "gender" -> it.copy(gender = value)
                "bloodGroup" -> it.copy(bloodGroup = value)
                "address" -> it.copy(address = value)
                "district" -> it.copy(district = value)
                else -> it
            }
        }
    }

    fun setAvailability(v: Boolean) = _uiState.update { it.copy(availability = v) }
    fun setLastDonation(millis: Long) = _uiState.update { it.copy(lastDonationDateMillis = millis) }
    fun setImageUri(uri: Uri?) = _uiState.update { it.copy(pendingImageUri = uri) }

    fun submit(onSuccess: () -> Unit) {
        val s = _uiState.value
        val validation = validate(s)
        if (validation != null) {
            _uiState.update { it.copy(error = validation) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            var imageUrl = s.profileImageUrl
            s.pendingImageUri?.let { uri ->
                donorRepository.uploadProfileImage(s.uid, uri)
                    .onSuccess { imageUrl = it }
                    .onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                        return@launch
                    }
            }
            val user = User(
                uid = s.uid,
                name = s.name.trim(),
                age = s.age.toIntOrNull() ?: 18,
                gender = s.gender,
                bloodGroup = s.bloodGroup,
                phone = s.phone,
                address = s.address.trim(),
                district = s.district,
                lastDonationDateMillis = s.lastDonationDateMillis,
                availability = s.availability,
                profileImageUrl = imageUrl
            )
            val result = if (s.isEditMode) donorRepository.updateDonor(user)
            else donorRepository.register(user)
            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, success = true) }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun validate(s: RegistrationUiState): String? {
        if (s.name.length < 2) return "Enter your full name"
        val age = s.age.toIntOrNull() ?: 0
        if (age !in 18..65) return "Age must be between 18 and 65"
        if (s.gender.isBlank()) return "Select gender"
        if (s.bloodGroup.isBlank()) return "Select blood group"
        if (s.address.length < 3) return "Enter your address"
        if (s.district.isBlank()) return "Select district"
        return null
    }
}

// ── Donor Search ──────────────────────────────────────────────────────────────

data class SearchUiState(
    val bloodGroup: String = "All",
    val district: String = "All",
    val availableOnly: Boolean = true,
    val donors: List<User> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class DonorSearchViewModel @Inject constructor(
    private val donorRepository: DonorRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun start() = search()

    fun setBloodGroup(g: String) { _uiState.update { it.copy(bloodGroup = g) }; search() }
    fun setDistrict(d: String) { _uiState.update { it.copy(district = d) }; search() }
    fun setAvailableOnly(v: Boolean) { _uiState.update { it.copy(availableOnly = v) }; search() }

    private fun search() {
        viewModelScope.launch {
            val s = _uiState.value
            donorRepository.searchDonors(
                bloodGroup = s.bloodGroup.takeIf { it != "All" },
                district = s.district.takeIf { it != "All" },
                availableOnly = s.availableOnly
            ).collect { donors ->
                _uiState.update { it.copy(loading = false, donors = donors) }
            }
        }
    }
}

// ── Emergency ─────────────────────────────────────────────────────────────────

data class EmergencyUiState(
    val bloodGroup: String = "",
    val hospitalName: String = "",
    val patientCondition: String = "",
    val unitsRequired: String = "1",
    val contactNumber: String = "",
    val locationText: String = "",
    val district: String = "",
    val alerts: List<EmergencyAlert> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val alertRepository: AlertRepository,
    private val donorRepository: DonorRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            donorRepository.observeCurrentDonor().collect { donor ->
                if (donor != null) {
                    _uiState.update {
                        it.copy(
                            bloodGroup = donor.bloodGroup,
                            contactNumber = donor.phone,
                            district = donor.district
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            alertRepository.observeAlerts().collect { alerts ->
                _uiState.update { it.copy(alerts = alerts) }
            }
        }
    }

    fun update(field: String, value: String) {
        _uiState.update {
            when (field) {
                "hospital" -> it.copy(hospitalName = value)
                "condition" -> it.copy(patientCondition = value)
                "units" -> it.copy(unitsRequired = value.filter { c -> c.isDigit() }.take(2))
                "contact" -> it.copy(contactNumber = value.filter { c -> c.isDigit() }.take(10))
                "location" -> it.copy(locationText = value)
                "bloodGroup" -> it.copy(bloodGroup = value)
                "district" -> it.copy(district = value)
                else -> it
            }
        }
    }

    fun createAlert(onSuccess: () -> Unit) {
        val s = _uiState.value
        if (s.bloodGroup.isBlank() || s.hospitalName.isBlank() || s.contactNumber.length != 10) {
            _uiState.update { it.copy(error = "Fill all required fields") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val donor = donorRepository.observeCurrentDonor().first()
            val alert = EmergencyAlert(
                requesterId = donor?.uid.orEmpty(),
                requesterName = donor?.name.orEmpty(),
                bloodGroup = s.bloodGroup,
                hospitalName = s.hospitalName.trim(),
                patientCondition = s.patientCondition.trim(),
                unitsRequired = s.unitsRequired.toIntOrNull() ?: 1,
                contactNumber = "+91${s.contactNumber}",
                locationText = s.locationText.trim(),
                district = s.district.ifBlank { donor?.district.orEmpty() }
            )
            alertRepository.createAlert(alert)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, success = true) }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun respond(alertId: String, accept: Boolean) {
        viewModelScope.launch {
            alertRepository.respondToAlert(alertId, accept)
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }
}

// ── Health ────────────────────────────────────────────────────────────────────

data class HealthUiState(
    val donor: User? = null,
    val eligible: Boolean = true,
    val countdown: String = "",
    val remainingMillis: Long = 0L,
    val lastDonationFormatted: String = ""
)

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val donorRepository: DonorRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            donorRepository.observeCurrentDonor().collect { donor ->
                val eligible = DateUtils.isEligible(donor?.lastDonationDateMillis ?: 0L)
                val remaining = DateUtils.remainingMillis(donor?.lastDonationDateMillis ?: 0L)
                _uiState.update {
                    HealthUiState(
                        donor = donor,
                        eligible = eligible,
                        countdown = DateUtils.formatCountdown(remaining),
                        remainingMillis = remaining,
                        lastDonationFormatted = DateUtils.formatDate(donor?.lastDonationDateMillis ?: 0L)
                    )
                }
            }
        }
    }
}

// ── Notifications ─────────────────────────────────────────────────────────────

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        val uid = authRepository.currentUid()
        if (uid != null) {
            viewModelScope.launch {
                notificationRepository.observeNotifications(uid).collect { list ->
                    _notifications.value = list
                    _loading.value = false
                }
            }
        } else {
            _loading.value = false
        }
    }

    fun markRead(id: String) {
        viewModelScope.launch { notificationRepository.markAsRead(id) }
    }

    fun markAllRead() {
        val uid = authRepository.currentUid() ?: return
        viewModelScope.launch { notificationRepository.markAllRead(uid) }
    }
}

// ── Admin ───────────────────────────────────────────────────────────────────

data class AdminUiState(
    val isAdmin: Boolean = false,
    val stats: AdminStats = AdminStats(),
    val donors: List<User> = emptyList(),
    val emergencies: List<EmergencyAlert> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun load() {
        val uid = authRepository.currentUid() ?: return
        viewModelScope.launch {
            val isAdmin = adminRepository.verifyAdmin(uid)
            _uiState.update { it.copy(isAdmin = isAdmin, loading = false) }
            if (!isAdmin) return@launch
            launch {
                adminRepository.observeStats().collect { stats ->
                    _uiState.update { it.copy(stats = stats) }
                }
            }
            launch {
                adminRepository.observeAllDonors().collect { donors ->
                    _uiState.update { it.copy(donors = donors) }
                }
            }
            launch {
                adminRepository.observeAllEmergencies().collect { emergencies ->
                    _uiState.update { it.copy(emergencies = emergencies) }
                }
            }
        }
    }

    fun suspendUser(uid: String, suspend: Boolean) {
        viewModelScope.launch {
            adminRepository.suspendUser(uid, suspend)
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }
}

// ── Settings / Theme ──────────────────────────────────────────────────────────

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository
) : ViewModel() {
    val isDarkMode = themeRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { themeRepository.setDarkMode(enabled) }
    }
}

// ── Session ───────────────────────────────────────────────────────────────────

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val donorRepository: DonorRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    suspend fun resolvePostAuthDestination(): String {
        val uid = authRepository.currentUid() ?: return com.jeevabindu.presentation.nav.Routes.Login
        val donor = donorRepository.getDonor(uid)
        return if (donor == null) com.jeevabindu.presentation.nav.Routes.Register
        else com.jeevabindu.presentation.nav.Routes.Main
    }
}
