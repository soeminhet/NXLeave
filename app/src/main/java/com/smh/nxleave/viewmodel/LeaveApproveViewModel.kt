package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.design.component.LeaveStatus
import com.smh.nxleave.domain.mapper.toUiModels
import com.smh.nxleave.domain.model.AccessLevel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.domain.repository.AuthRepository
import com.smh.nxleave.domain.repository.FireStoreRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import com.smh.nxleave.screen.model.LeaveRequestUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveApproveViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val fireStoreRepository: FireStoreRepository,
    private val realTimeDataRepository: RealTimeDataRepository
): ViewModel() {

    private var _uiState = MutableStateFlow(LeaveApproveUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchCurrentStaff()
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

    private fun fetchCurrentStaff() {
        viewModelScope.launch(Dispatchers.IO) {
            realTimeDataRepository.getCurrentStaff()
                .collectLatest { currentStaff ->
                    _uiState.update {
                        it.copy(currentStaff = currentStaff)
                    }
                    fetchRelatedStaffIds(currentStaff)
                }
        }
    }

    private suspend fun fetchRelatedStaffIds(currentStaff: StaffModel){
        val adminRoles = fireStoreRepository.getAllRoles().filter { it.accessLevel == AccessLevel.All() }
        val isAdmin = adminRoles.any { it.id == currentStaff.roleId }

        realTimeDataRepository.getRelatedStaffBy(currentStaff.currentProjectIds)
            .map { staves ->
                if (isAdmin) staves
                else staves.filterNot { staff -> adminRoles.any { it.id == staff.roleId } }
            }
            .map { it.map { staff -> staff.id } }
            .distinctUntilChanged()
            .collectLatest { relatedStaffIds ->
                if (relatedStaffIds.isNotEmpty()) {
                    fetchLeaveRequest(relatedStaffIds)
                }
            }
    }

    private suspend fun fetchLeaveRequest(relatedStaffIds: List<String>) {
        combine(
            realTimeDataRepository.getLeaveRequestBy(relatedStaffIds),
            realTimeDataRepository.getAllLeaveTypes(),
            realTimeDataRepository.getAllStaves(),
            realTimeDataRepository.getAllRoles(),
            realTimeDataRepository.getAllProjects()
        ) { leaveRequests, leaveTypes, staves, roles, projects ->
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
        }.collect()
    }
}

data class LeaveApproveUiState(
    val leaveRequests: List<LeaveRequestUiModel> = emptyList(),
    val currentStaff: StaffModel? = null
)