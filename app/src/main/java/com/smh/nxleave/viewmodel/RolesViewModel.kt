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
            val models = fireStoreRepository.getAllRoles()
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
                setLoading(false)
                return@launch
            }
            val result = fireStoreRepository.addRole(model)
            if (result) {
                fetchRoles()
            } else {
                // TODO: Show Error
                setLoading(false)
            }
        }
    }

    fun updateRole(model: RoleModel) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            if (checkExist(model.name)) {
                setLoading(false)
                return@launch
            }
            val result = fireStoreRepository.updateRole(model)
            if (result) {
                fetchRoles()
            } else {
                // TODO: Show Error
                setLoading(false)
            }
        }
    }

    private suspend fun checkExist(name: String): Boolean {
        val trimmed = name.removeWhiteSpaces()
        val exist = uiState.value.roles.any { role ->
            role.name.removeWhiteSpaces().equals(trimmed, ignoreCase = true)
        }
        if (exist) _uiEvent.emit(RoleUiEvent.RoleExist)
        return exist
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