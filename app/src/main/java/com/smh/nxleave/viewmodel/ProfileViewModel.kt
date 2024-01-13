package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.model.AccessLevel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.repository.AuthRepository
import com.smh.nxleave.domain.repository.FireStoreRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val realTimeDataRepository: RealTimeDataRepository,
    private val fireStoreRepository: FireStoreRepository,
    private val authRepository: AuthRepository,
): ViewModel() {

    private var staffId = ""
    private var role: RoleModel? = null

    private var _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchStaffInfo()
        observeAccessLevel()
    }

    private fun observeAccessLevel() {
        viewModelScope.launch(Dispatchers.IO) {
            realTimeDataRepository.currentStaff
                .combine(realTimeDataRepository.roles) { staff, roles ->
                    roles.firstOrNull { role -> role.id == staff?.roleId }?.accessLevel ?: AccessLevel.None()
                }
                .map {
                    it == AccessLevel.All()
                }
                .distinctUntilChanged()
                .collectLatest { show ->
                    _uiState.update {
                        it.copy(
                            showManagement = show
                        )
                    }
                }
        }
    }

    private fun fetchStaffInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            realTimeDataRepository.currentStaff
                .collectLatest { model ->
                    if (model != null) {
                        role = fireStoreRepository.getRole(model.roleId)
                        _uiState.update {
                            it.copy(
                                name = model.name,
                                photoURL = model.photo,
                                roleName = role?.name ?: ""
                            )
                        }
                    }
                }
        }
    }

    fun logout() {
        staffId = ""
        role = null
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