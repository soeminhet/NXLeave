package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.model.AccessLevel
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.domain.repository.FireStoreRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import com.smh.nxleave.utility.removeWhiteSpaces
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val realTimeDataRepository: RealTimeDataRepository,
    private val fireStoreRepository: FireStoreRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProjectsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        fetchProjects()
    }

    private fun fetchProjects() {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val allStaves = realTimeDataRepository.staves.value
            val approveRoles = realTimeDataRepository.roles.value.filter { it.accessLevel == AccessLevel.Approve() }
            val approveStaves = allStaves.filter { approveRoles.any { role -> role.id == it.roleId } }
            val models = fireStoreRepository.getAllProjects().map {
                val managers =
                    approveStaves.filter { staff -> staff.currentProjectIds.contains(it.id) }
                        .joinToString { staff -> staff.name }
                it.copy(
                    managerName = managers
                )
            }
            _uiState.update {
                it.copy(
                    projects = models
                )
            }
            setLoading(false)
        }
    }

    fun addProject(model: ProjectModel) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            if (checkExist(model.name)) {
                setLoading(false)
                return@launch
            }
            val adminRoles = realTimeDataRepository.roles.value.filter { it.accessLevel == AccessLevel.All() }
            val admins = realTimeDataRepository.staves.value.filter {
                adminRoles.any { role -> role.id == it.roleId}
            }
            val result = fireStoreRepository.addProject(model, admins)
            if (result) {
                fetchProjects()
            } else {
                // TODO: Show Error
                setLoading(false)
            }
        }
    }

    fun updateProject(model: ProjectModel) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            if (checkExist(model.name)) {
                setLoading(false)
                return@launch
            }
            val result = fireStoreRepository.updateProject(model)
            if (result) {
                fetchProjects()
            } else {
                // TODO: Show Error
                setLoading(false)
            }
        }
    }

    private suspend fun checkExist(name: String): Boolean {
        val trimmed = name.removeWhiteSpaces()
        val exist = uiState.value.projects.any { project ->
            project.name.removeWhiteSpaces().equals(trimmed, ignoreCase = true)
        }
        if (exist) _uiEvent.emit(ProjectsUiEvent.ProjectExist)
        return exist
    }

    private fun setLoading(loading: Boolean) {
        _uiState.update {
            it.copy(loading = loading)
        }
    }
}

data class ProjectsUiState(
    val loading: Boolean = false,
    val projects: List<ProjectModel> = emptyList(),
)

sealed interface ProjectsUiEvent {
    data object ProjectExist: ProjectsUiEvent
}