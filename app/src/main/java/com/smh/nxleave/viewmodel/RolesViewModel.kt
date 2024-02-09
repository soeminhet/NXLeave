package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.repository.FireStoreRepository
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
class RolesViewModel @Inject constructor(
    private val fireStoreRepository: FireStoreRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(RolesUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<RoleUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        fetchRoles()
    }

    private fun fetchRoles() {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val models = fireStoreRepository.getAllRoles().sortedBy { it.name }
            _uiState.update {
                it.copy(
                    roles = models
                )
            }
            setLoading(false)
        }
    }

    fun addRole(model: RoleModel) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            if (checkExist(model.name)) {
                _uiEvent.emit(RoleUiEvent.RoleExist)
                setLoading(false)
                return@launch
            }
            val result = fireStoreRepository.addRole(model)
            if (result) fetchRoles()
            else setLoading(false)
        }
    }

    fun updateRole(model: RoleModel) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val origin = uiState.value.roles.first{ it.id == model.id }
            val isNameChange = !origin.name.removeWhiteSpaces().equals(model.name.removeWhiteSpaces(), ignoreCase = true)
            if (isNameChange && checkExist(model.name)) {
                _uiEvent.emit(RoleUiEvent.RoleExist)
                setLoading(false)
                return@launch
            }
            val result = fireStoreRepository.updateRole(model)
            if (result) fetchRoles()
            else setLoading(false)
        }
    }

    fun updateRoleEnable(model: RoleModel) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = fireStoreRepository.updateRole(model)
            if (result) fetchRoles()
            else setLoading(false)
        }
    }

    private fun checkExist(name: String): Boolean {
        val trimmed = name.removeWhiteSpaces()
        return uiState.value.roles.any { role ->
            role.name.removeWhiteSpaces().equals(trimmed, ignoreCase = true)
        }
    }

    private fun setLoading(loading: Boolean) {
        _uiState.update {
            it.copy(loading = loading)
        }
    }
}

data class RolesUiState(
    val loading: Boolean = false,
    val roles: List<RoleModel> = emptyList()
)

sealed interface RoleUiEvent {
    data object RoleExist: RoleUiEvent
}