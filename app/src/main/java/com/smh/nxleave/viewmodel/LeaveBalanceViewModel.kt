package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.mapper.toModel
import com.smh.nxleave.domain.mapper.toUiModel
import com.smh.nxleave.domain.model.LeaveBalanceModel
import com.smh.nxleave.domain.repository.FireStoreRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import com.smh.nxleave.screen.model.LeaveBalanceUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LeaveBalanceViewModel @Inject constructor(
    private val realTimeDataRepository: RealTimeDataRepository,
    private val fireStoreRepository: FireStoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaveBalanceUiState())
    val uiState = _uiState.asStateFlow()

    private var cacheExistLeaveBalances: List<LeaveBalanceModel> = emptyList()

    init {
        fetchAllLeaveBalances()
    }

    private fun fetchAllLeaveBalances() {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val roles = realTimeDataRepository.roles.value
            val leaveTypes = realTimeDataRepository.leaveTypes.value
            val leaveBalances = fireStoreRepository.getAllLeaveBalance()

            cacheExistLeaveBalances = leaveBalances
            val mapped = roles.associate { role ->
                val filterLeaveBalances = leaveBalances.filter { it.roleId == role.id }
                val list = leaveTypes
                    .filter { it.enable }
                    .map { type ->
                        val existLeaveBalance =
                            filterLeaveBalances.firstOrNull { it.leaveTypeId == type.id }
                        existLeaveBalance?.toUiModel(
                            leaveTypeModel = type
                        ) ?: LeaveBalanceUiModel(
                            id = UUID.randomUUID().toString(),
                            roleId = role.id,
                            leaveTypeId = type.id,
                            leaveTypeName = type.name,
                            balance = "0"
                        )
                    }
                Pair(role.name, list)
            }

            _uiState.update {
                it.copy(
                    leaveBalanceMap = mapped
                )
            }
            setLoading(false)
        }
    }

    fun updateLeaveBalances(list: List<LeaveBalanceUiModel>) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            list.forEach { uiModel ->
                if (cacheExistLeaveBalances.any { it.id == uiModel.id }) {
                    fireStoreRepository.updateLeaveBalance(uiModel.toModel())
                } else {
                    fireStoreRepository.addLeaveBalance(uiModel.toModel())
                }
            }
            fetchAllLeaveBalances()
        }
    }

    private fun setLoading(loading: Boolean) {
        _uiState.update {
            it.copy(loading = loading)
        }
    }
}

data class LeaveBalanceUiState(
    val loading: Boolean = false,
    val leaveBalanceMap: Map<String, List<LeaveBalanceUiModel>> = emptyMap()
)