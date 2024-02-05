package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.model.AccessLevel
import com.smh.nxleave.domain.repository.AuthRepository
import com.smh.nxleave.domain.repository.FireStoreRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val realTimeDataRepository: RealTimeDataRepository,
    private val fireStoreRepository: FireStoreRepository,
    private val authRepository: AuthRepository,
): ViewModel() {
    private var _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchStaffInfo()
    }

    private fun fetchStaffInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            realTimeDataRepository.getCurrentStaff()
                .collectLatest { model ->
                    val role = fireStoreRepository.getRole(model.roleId)
                    _uiState.update {
                        it.copy(
                            name = model.name,
                            photoURL = model.photo,
                            roleName = role?.name ?: "",
                            showManagement = role?.accessLevel == AccessLevel.All()
                        )
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.updateStaffId("")
        }
    }
}

data class ProfileUiState(
    val photoURL: String = "",
    val name: String = "",
    val roleName: String = "",
    val showManagement: Boolean = false,
)