package com.smh.nxleave.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.smh.nxleave.design.component.LeaveStatus
import com.smh.nxleave.domain.mapper.toUiModels
import com.smh.nxleave.domain.model.AccessLevel
import com.smh.nxleave.domain.model.LeaveBalanceModel
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.domain.repository.AuthRepository
import com.smh.nxleave.domain.repository.FireStoreRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepositoryV2
import com.smh.nxleave.screen.model.LeaveRequestModel
import com.smh.nxleave.screen.model.LeaveRequestUiModel
import com.smh.nxleave.screen.model.MyLeaveBalanceUiModel
import com.smh.nxleave.utility.combine
import com.smh.nxleave.utility.toDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val fireStoreRepository: FireStoreRepository,
    private val authRepository: AuthRepository,
    private val realTimeDataRepositoryV2: RealTimeDataRepositoryV2,
): ViewModel() {

    private var _uiState = MutableStateFlow(BalanceUiState())
    val uiState = _uiState.asStateFlow()

    private var _uiEvent = MutableSharedFlow<BalanceUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var leaveRequestListener: ListenerRegistration? = null
    private var currentStaff: StaffModel? = null
    private var roles: List<RoleModel> = emptyList()

    init {
        fetchLeaveRequest()
    }

    private fun fetchLeaveRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                realTimeDataRepositoryV2.getLeaveRequestBy(authRepository.cacheStaffId),
                realTimeDataRepositoryV2.getAllLeaveTypes(),
                realTimeDataRepositoryV2.getAllStaves(),
                realTimeDataRepositoryV2.getAllRoles(),
                realTimeDataRepositoryV2.getAllProjects(),
                fetchStaffBalance()
            ) { leaveRequests, leaveTypes, staves, roles, projects, balances ->
                this@BalanceViewModel.roles = roles

                val filteredLeaveTypes = leaveTypes.filter { it.enable }
                val filteredLeaveRequests = leaveRequests.filter {
                    filteredLeaveTypes.any { type -> type.id == it.leaveTypeId } && it.leaveStatus != LeaveStatus.Rejected.name
                }

                val tookDays = filteredLeaveRequests.toTookDays(leaveTypes = leaveTypes)
                val totalDays = balances.toTotalDays(leaveTypes = filteredLeaveTypes)
                val leaveTookPercentages = filteredLeaveRequests.toPercentages(
                    leaveTypes = filteredLeaveTypes,
                    totalDays = totalDays
                )
                val myLeaveBalances = balances.toMyBalances(
                    leaveRequests = filteredLeaveRequests,
                    leaveTypes = filteredLeaveTypes
                )
                val leaveRequestUiModels = leaveRequests.toUiModels(
                    staves = staves,
                    leaveTypes = leaveTypes,
                    roles = roles,
                    projects = projects
                )

                _uiState.update { uiState ->
                    uiState.copy(
                        totalDays = totalDays,
                        tookDays = tookDays,
                        leaveRequestModels = leaveRequests,
                        leaveTookPercentages = leaveTookPercentages,
                        leaveRequests = leaveRequestUiModels,
                        balances = balances,
                        leaveTypes = leaveTypes,
                        myLeaveBalances = myLeaveBalances
                    )
                }
            }.collect()
        }
    }

    private fun fetchStaffBalance(): Flow<List<LeaveBalanceModel>> {
        return callbackFlow {
            realTimeDataRepositoryV2.getCurrentStaff()
                .collectLatest { staff ->
                    currentStaff = staff
                    realTimeDataRepositoryV2.getLeaveBalanceBy(staff.roleId)
                        .collectLatest { balances ->
                            send(balances)
                        }
                }
            awaitClose()
        }
    }

    fun submitLeaveRequest(model: LeaveRequestModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val leftLeaves = leaveLeftFor(model)
            if (leftLeaves >= model.duration) {
                currentStaff?.let { staff ->
                    val adminRoles = roles.filter { it.accessLevel == AccessLevel.All() }
                    val isAdmin = adminRoles.any { role -> role.id == staff.roleId }

                    val updatedModel = if (isAdmin) {
                        model.copy(
                            staffId = authRepository.cacheStaffId,
                            leaveStatus = LeaveStatus.Approved.name,
                            leaveApprovedDate = OffsetDateTime.now(),
                            approverId = authRepository.cacheStaffId
                        )
                    } else {
                        model.copy(staffId = authRepository.cacheStaffId)
                    }
                    fireStoreRepository.addLeaveRequest(updatedModel)
                }
            } else {
                val leaveTypeName = uiState.value.leaveTypes.first{ type -> type.id == model.leaveTypeId }.name
                _uiEvent.emit(BalanceUiEvent.NotEnoughLeave(leaveTypeName, leftLeaves))
            }
        }
    }

    fun deleteLeaveRequest(model: LeaveRequestUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            fireStoreRepository.deleteLeaveRequest(model.id)
        }
    }

    private fun leaveLeftFor(model: LeaveRequestModel): Double {
        uiState.value.run {
            val leaveBalance = balances.firstOrNull { balance ->
                balance.leaveTypeId == model.leaveTypeId
            }?.balance ?: 0
            val tookLeaves = leaveRequestModels
                .filter { request -> request.leaveTypeId == model.leaveTypeId }
                .sumOf { request -> request.duration }

            return leaveBalance - tookLeaves
        }
    }

    override fun onCleared() {
        leaveRequestListener?.remove()
        super.onCleared()
    }
}

data class BalanceUiState(
    val loading: Boolean = false,
    val totalDays: Double = 0.0,
    val tookDays: Double = 0.0,
    val balances: List<LeaveBalanceModel> = emptyList(),
    val leaveTypes: List<LeaveTypeModel> = emptyList(),
    val leaveRequests: List<LeaveRequestUiModel> = emptyList(),
    val leaveTookPercentages: Map<Color, Int> = emptyMap(),
    val leaveRequestModels: List<LeaveRequestModel> = emptyList(),
    val myLeaveBalances: List<MyLeaveBalanceUiModel> = emptyList()
) {
    val leftDays: String get() = (totalDays - tookDays).toDays()
}

sealed interface BalanceUiEvent {
    data class NotEnoughLeave(val leaveTypeName: String, val leftDays: Double): BalanceUiEvent
}


/** Private Extensions */

private fun List<LeaveBalanceModel>.toMyBalances(leaveRequests: List<LeaveRequestModel>, leaveTypes: List<LeaveTypeModel>): List<MyLeaveBalanceUiModel> {
    val tookDaysByLeaveType = leaveRequests
        .groupBy { request -> request.leaveTypeId }
        .map { group -> Pair(group.key, group.value.sumOf { request ->  request.duration }) }
        .toMap()

    return this.mapNotNull { balance ->
        val type = leaveTypes.firstOrNull { type -> type.id == balance.leaveTypeId } ?: return@mapNotNull null
        MyLeaveBalanceUiModel(
            id = balance.id,
            color = Color(type.color),
            name = type.name,
            took = tookDaysByLeaveType[balance.leaveTypeId] ?: 0.0,
            total = balance.balance.toDouble(),
            enable = type.enable
        )
    }
}

private fun List<LeaveBalanceModel>.toTotalDays(leaveTypes: List<LeaveTypeModel>): Double {
    return this
        .filter { b -> leaveTypes.any { t -> t.id == b.leaveTypeId } }
        .sumOf { b -> b.balance }
        .toDouble()
}

private fun List<LeaveRequestModel>.toTookDays(leaveTypes: List<LeaveTypeModel>): Double {
    return this
        .filter { request -> leaveTypes.firstOrNull { t -> t.id == request.leaveTypeId }?.enable ?: false }
        .sumOf { request -> request.duration }
}

private fun List<LeaveRequestModel>.toPercentages(
    leaveTypes: List<LeaveTypeModel>,
    totalDays: Double
): Map<Color, Int> {
    return this
        .groupBy { request -> request.leaveTypeId }
        .toSortedMap()
        .mapNotNull{ group ->
            val color = leaveTypes.firstOrNull { type -> type.id == group.key }?.color ?: return@mapNotNull null
            val totalDuration = group.value.sumOf { request ->  request.duration }
            val percentage = ((totalDuration / totalDays) * 100).toInt()
            Pair(
                Color(color = color),
                percentage
            )
        }
        .groupBy { it.first }
        .mapValues { group -> group.value.sumOf { it.second } }
}