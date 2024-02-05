package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.smh.nxleave.domain.mapper.toUiModels
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepositoryV2
import com.smh.nxleave.screen.model.LeaveRequestUiModel
import com.smh.nxleave.utility.DATE_PATTERN_THREE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val realTimeDataRepository: RealTimeDataRepository,
    private val realTimeDataRepositoryV2: RealTimeDataRepositoryV2
): ViewModel() {

    private var _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    private var leaveRequestListener: ListenerRegistration? = null
    private var upcomingEventListener: ListenerRegistration? = null

    init {
        fetchCurrentStaff()
        fetchUpcomingEvents()
    }

    private fun fetchCurrentStaff() {
        viewModelScope.launch(Dispatchers.IO) {
            realTimeDataRepositoryV2.getCurrentStaff()
                .collectLatest { currentStaff ->
                    _uiState.update {
                        it.copy(currentStaff = currentStaff)
                    }
                    fetchRelatedStaffIds(currentStaff.currentProjectIds)
                }
        }
    }

    private suspend fun fetchRelatedStaffIds(projectIds: List<String>){
        realTimeDataRepositoryV2.getRelatedStaffBy(projectIds)
            .map { it.map { staff -> staff.id } }
            .distinctUntilChanged()
            .collectLatest { relatedStaffIds ->
                if (relatedStaffIds.isNotEmpty()) {
                    fetchLeaveRequest(relatedStaffIds)
                }
            }
    }

    private suspend fun fetchLeaveRequest(relatedStaffIds: List<String>) {
        realTimeDataRepositoryV2.getLeaveRequestBy(relatedStaffIds)
            .collectLatest { leaveRequests ->
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

    private fun fetchUpcomingEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            realTimeDataRepositoryV2.getAllUpcomingEvent()
                .collectLatest { events ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            upcomingEvents = events
                        )
                    }
                }
        }
    }

    override fun onCleared() {
        leaveRequestListener?.remove()
        upcomingEventListener?.remove()
        super.onCleared()
    }
}

data class DashboardUiState(
    val leaveRequests: List<LeaveRequestUiModel> = emptyList(),
    val upcomingEvents: List<EventModel> = emptyList(),
    val currentStaff: StaffModel? = null,
    val todayDate: String = "Today, ${OffsetDateTime.now().format(DATE_PATTERN_THREE)}"
)