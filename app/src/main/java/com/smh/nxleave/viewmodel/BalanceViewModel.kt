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
import com.smh.nxleave.domain.repository.AuthRepository
import com.smh.nxleave.domain.repository.FireStoreRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import com.smh.nxleave.screen.model.LeaveRequestModel
import com.smh.nxleave.screen.model.LeaveRequestUiModel
import com.smh.nxleave.screen.model.MyLeaveBalanceUiModel
import com.smh.nxleave.utility.toDays
import com.smh.nxleave.utility.toTimeStamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val realTimeDataRepository: RealTimeDataRepository,
    private val fireStoreRepository: FireStoreRepository,
    private val authRepository: AuthRepository,
): ViewModel() {

    private var _uiState = MutableStateFlow(BalanceUiState())
    val uiState = _uiState.asStateFlow()

    private var _uiEvent = MutableSharedFlow<BalanceUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var leaveRequestListener: ListenerRegistration? = null

    init {
        listenLeaveRequest()
    }

    fun refresh() {
        listenLeaveRequest()
    }

    private fun listenLeaveRequest() {
        setLoading(true)
        leaveRequestListener?.remove()
        leaveRequestListener = fireStoreRepository.getLeaveRequestBy(authRepository.cacheStaffId){
            it.onSuccess { leaveRequests ->
                val leaveTypes = realTimeDataRepository.leaveTypes.value
                val staves = realTimeDataRepository.staves.value
                val roles = realTimeDataRepository.roles.value
                val projects = realTimeDataRepository.projects.value
                val balances = realTimeDataRepository.currentStaffLeaveBalance.value

                val tookDays = leaveRequests.toTookDays(leaveTypes = leaveTypes)
                val totalDays = balances.toTotalDays(leaveTypes = leaveTypes)
                val leaveTookPercentages = leaveRequests.toPercentages(
                    leaveTypes = leaveTypes.filter { type -> type.enable },
                    totalDays = totalDays
                )
                val leaveRequestUiModels = leaveRequests.toUiModels(
                    staves = staves,
                    leaveTypes = leaveTypes,
                    roles = roles,
                    projects = projects
                )
                val myLeaveBalances = balances.toMyBalances(leaveRequests = leaveRequests, leaveTypes = leaveTypes)

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
                setLoading(false)
            }
        }
    }

    fun submitLeaveRequest(model: LeaveRequestModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val leftLeaves = leaveLeftFor(model)
            if (leftLeaves >= model.duration) {
                realTimeDataRepository.currentStaff.value?.let { currentStaff ->
                    val adminRoles = realTimeDataRepository.roles.value.filter { it.accessLevel == AccessLevel.All() }
                    val isAdmin = adminRoles.any { role -> role.id == currentStaff.roleId }
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
        .filter { b -> leaveTypes.firstOrNull { t -> t.id == b.leaveTypeId }?.enable ?: false }
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