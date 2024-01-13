package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.repository.FireStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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
            val result = fireStoreRepository.updateRole(model)
            if (result) {
                fetchRoles()
            } else {
                // TODO: Show Error
                setLoading(false)
            }
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