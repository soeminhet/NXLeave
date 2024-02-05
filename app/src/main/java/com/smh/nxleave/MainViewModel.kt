package com.smh.nxleave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.model.AccessLevel
import com.smh.nxleave.domain.repository.AuthRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    realTimeDataRepository: RealTimeDataRepository
): ViewModel() {
    val isLogIn = authRepository.staffIdFlow
        .distinctUntilChanged()
        .map { it.isNotEmpty() }
        .stateIn(
            initialValue = false,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000)
        )

    val isAccountEnable = realTimeDataRepository.getCurrentStaff()
        .combine(isLogIn) { staff, login ->
            if(login) staff.enable
            else true
        }

    val accessLevel = realTimeDataRepository.getCurrentStaff()
        .combine(realTimeDataRepository.getAllRoles()) { staff, roles ->
            roles.firstOrNull { role -> role.id == staff.roleId }?.accessLevel ?: AccessLevel.None()
        }

    fun onLogout() {
        viewModelScope.launch {
            authRepository.updateStaffId("")
        }
    }
}