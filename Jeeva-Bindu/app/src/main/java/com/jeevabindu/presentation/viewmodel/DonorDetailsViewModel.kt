package com.jeevabindu.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeevabindu.domain.model.User
import com.jeevabindu.domain.repository.DonorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DonorDetailsViewModel @Inject constructor(
    private val donorRepository: DonorRepository
) : ViewModel() {
    private val _donor = MutableStateFlow<User?>(null)
    val donor: StateFlow<User?> = _donor.asStateFlow()

    fun load(donorId: String) {
        viewModelScope.launch {
            _donor.value = donorRepository.getDonor(donorId)
        }
    }
}
