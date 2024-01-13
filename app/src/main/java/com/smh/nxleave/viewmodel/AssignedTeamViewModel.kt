package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignedTeamViewModel @Inject constructor(
    private val realTimeDataRepository: RealTimeDataRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(AssignedTeamUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            realTimeDataRepository.currentStaff
                .combine(realTimeDataRepository.projects) { staff, projects ->
                    staff?.currentProjectIds?.mapNotNull {
                        projects.firstOrNull { project ->
                             project.id == it
                        }?.name
                    }.orEmpty()
                }
                .collectLatest { model ->
                    _uiState.update {
                        it.copy(
                            teams = model
                        )
                    }
                }
        }
    }
}

data class AssignedTeamUiState(
    val teams: List<String> = emptyList()
)