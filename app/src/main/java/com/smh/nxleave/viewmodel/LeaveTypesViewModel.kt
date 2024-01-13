package com.smh.nxleave.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.repository.FireStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveTypesViewModel @Inject constructor(
    private val fireStoreRepository: FireStoreRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(LeaveTypesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchLeaveTypes()
    }

    private fun fetchLeaveTypes() {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val models = fireStoreRepository.getAllLeaveTypes()
            _uiState.update {
                it.copy(
                    leaveTypes = models
                )
            }
            setLoading(false)
        }
    }

    fun addLeaveType(name: String, color: Long) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = fireStoreRepository.addLeaveType(name, color)
            if (result) {
                fetchLeaveTypes()
            } else {
                // TODO: Show Error
                setLoading(false)
            }
        }
    }

    fun updateLeaveType(model: LeaveTypeModel) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = fireStoreRepository.updateLeaveType(model)
            if (result) {
                fetchLeaveTypes()
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

data class LeaveTypesUiState(
    val loading: Boolean = false,
    val leaveTypes: List<LeaveTypeModel> = emptyList()
)