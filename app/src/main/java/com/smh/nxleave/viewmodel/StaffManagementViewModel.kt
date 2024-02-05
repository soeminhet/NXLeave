package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.mapper.toModel
import com.smh.nxleave.domain.mapper.toUIModels
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.domain.repository.AuthRepository
import com.smh.nxleave.domain.repository.FireStoreRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import com.smh.nxleave.screen.model.StaffProfileUiModel
import com.smh.nxleave.utility.removeWhiteSpaces
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffManagementViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val fireStoreRepository: FireStoreRepository,
    private val realTimeDataRepository: RealTimeDataRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(StaffManagementUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<StaffManagementUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        fetchStaves()
        fetchRoles()
        fetchProjects()
    }

    private fun fetchStaves() {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val staves = fireStoreRepository.getAllStaff().sortedBy { it.name }
            val roles = fireStoreRepository.getAllRoles()
            val mappedStaves = staves.toUIModels(roles)
            _uiState.update {
                it.copy(staves = mappedStaves)
            }
            setLoading(false)
        }
    }

    private fun fetchRoles() {
        viewModelScope.launch(Dispatchers.IO) {
            realTimeDataRepository.getAllRoles()
                .collectLatest { roles ->
                    _uiState.update { state ->
                        state.copy(
                            enableRoles = roles.filter { it.enable }
                        )
                    }
                }
        }
    }

    private fun fetchProjects() {
        viewModelScope.launch(Dispatchers.IO) {
            realTimeDataRepository.getAllProjects()
                .collectLatest { projects ->
                    _uiState.update { state ->
                        state.copy(
                            enableProjects = projects.filter { it.enable }
                        )
                    }
                }
        }
    }

    fun mangeAccount(model: StaffModel, password: String) {
        setLoading(true)
        viewModelScope.launch {
            if (checkExist(model.name)) {
                _uiEvent.emit(StaffManagementUiEvent.AccountExist)
                setLoading(false)
                return@launch
            }
            if (password.isNotBlank()) {
                viewModelScope.launch(Dispatchers.IO) {
                    authRepository.singUp(model.email, password) {
                        it
                            .onSuccess { authModel ->
                                saveStaff(model.copy(id = authModel.id))
                            }
                            .onFailure {
                                setLoading(false)
                                viewModelScope.launch {
                                    _uiEvent.emit(StaffManagementUiEvent.AccountCreateError)
                                }
                            }
                    }
                }
            } else {
                updateStaff(model)
            }
        }
    }

    fun updateStaffEnable(model: StaffProfileUiModel) {
        setLoading(true)
        updateStaff(model.toModel())
    }

    private fun updateStaff(model: StaffModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = fireStoreRepository.updateStaff(model)
            if (!success) _uiEvent.emit(StaffManagementUiEvent.UpdateStaffError)
            fetchStaves()
        }
    }

    private fun saveStaff(model: StaffModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = fireStoreRepository.addStaff(model)
            if (!success) _uiEvent.emit(StaffManagementUiEvent.SaveStaffError)
            fetchStaves()
        }
    }

    private fun checkExist(name: String): Boolean {
        val trimmed = name.removeWhiteSpaces()
        return uiState.value.staves.any { staff ->
            staff.name.removeWhiteSpaces().equals(trimmed, ignoreCase = true)
        }
    }

    private fun setLoading(loading: Boolean) {
        _uiState.update {
            it.copy(loading = loading)
        }
    }
}

data class StaffManagementUiState(
    val loading: Boolean = false,
    val staves: List<StaffProfileUiModel> = emptyList(),
    val enableRoles: List<RoleModel> = emptyList(),
    val enableProjects: List<ProjectModel> = emptyList()
)

sealed interface StaffManagementUiEvent {
    data object AccountCreateError: StaffManagementUiEvent
    data object SaveStaffError: StaffManagementUiEvent
    data object UpdateStaffError: StaffManagementUiEvent
    data object AccountExist: StaffManagementUiEvent
}