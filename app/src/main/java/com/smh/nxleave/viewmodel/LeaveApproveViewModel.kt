package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.smh.nxleave.design.component.LeaveStatus
import com.smh.nxleave.domain.mapper.toUiModels
import com.smh.nxleave.domain.model.AccessLevel
import com.smh.nxleave.domain.repository.AuthRepository
import com.smh.nxleave.domain.repository.FireStoreRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import com.smh.nxleave.screen.model.LeaveRequestUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveApproveViewModel @Inject constructor(
    private val realTimeDataRepository: RealTimeDataRepository,
    private val authRepository: AuthRepository,
    private val fireStoreRepository: FireStoreRepository,
): ViewModel() {

    private var _uiState = MutableStateFlow(LeaveApproveUiState())
    val uiState = _uiState.asStateFlow()

    private var leaveRequestListener: ListenerRegistration? = null

    val accessLevel = realTimeDataRepository.currentStaff
        .combine(realTimeDataRepository.roles) { staff, roles ->
            roles.firstOrNull { role -> role.id == staff?.roleId }?.accessLevel ?: AccessLevel.None()
        }

    init {
        observeRelatedStaffIds()
    }

    private fun getAdminRoleIds(): List<String> {
        return realTimeDataRepository.roles.value.filter { role -> role.accessLevel is AccessLevel.All }.map { it.id }
    }

    private fun observeRelatedStaffIds() {
        viewModelScope.launch(Dispatchers.IO) {
            realTimeDataRepository.relatedStaves
                .combine(accessLevel) { staves, access ->
                    if (access is AccessLevel.All) staves
                    else {
                        val adminRoleIds = getAdminRoleIds()
                        staves.filterNot { staff -> adminRoleIds.contains(staff.roleId) }
                    }
                }
                .onEach { setLoading(true) }
                .map { it.map { staff -> staff.id } }
                .distinctUntilChanged()
                .collectLatest { relatedStaffIds ->
                    setLoading(false)
                    if (relatedStaffIds.isNotEmpty()) {
                        listenLeaveRequest(relatedStaffIds)
                    }
                }
        }
    }

    private fun listenLeaveRequest(relatedStaffIds: List<String>) {
        leaveRequestListener?.remove()
        leaveRequestListener = fireStoreRepository.getLeaveRequestBy(relatedStaffIds){
            it.onSuccess { leaveRequests ->
                val leaveTypes = realTimeDataRepository.leaveTypes.value
                val staves = realTimeDataRepository.staves.value
                val roles = realTimeDataRepository.roles.value
                val projects = realTimeDataRepository.projects.value
                val leaveRequestUiModels = leaveRequests.toUiModels(
                    roles = roles,
                    staves = staves,
                    leaveTypes = leaveTypes,
                    projects = projects
                )
                _uiState.update { uiState ->
                    uiState.copy(
                        leaveRequests = leaveRequestUiModels,
                    )
                }
            }
        }
    }

    fun updateLeaveStatus(id: String, status: LeaveStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            fireStoreRepository.updateLeaveRequestStatus(
                id = id,
                status = status,
                approverId = authRepository.cacheStaffId
            )
        }
    }

    private fun setLoading(loading: Boolean) {
        _uiState.update {
            it.copy(
                loading = loading
            )
        }
    }

    override fun onCleared() {
        leaveRequestListener?.remove()
        super.onCleared()
    }
}

data class LeaveApproveUiState(
    val loading: Boolean = false,
    val leaveRequests: List<LeaveRequestUiModel> = emptyList()
)