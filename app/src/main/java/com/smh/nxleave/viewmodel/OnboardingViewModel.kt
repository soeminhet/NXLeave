package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.model.AccessLevel
import com.smh.nxleave.domain.model.AuthUserModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.domain.repository.AuthRepository
import com.smh.nxleave.domain.repository.FireStoreRepository
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
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val fireStoreRepository: FireStoreRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<OnboardingUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        fetchIsInitialized()
    }

    private fun fetchIsInitialized() {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    isInitialized = fireStoreRepository.isInitialized()
                )
            }
        }.invokeOnCompletion {
            setLoading(false)
        }
    }

    fun getStarted() {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val success = fireStoreRepository.addRole(
                RoleModel(id = "", name = "Admin", enable = true, accessLevel = AccessLevel.All())
            )
            if (success) {
                authRepository.singUp(
                    email = "admin@nxleave.co",
                    password = "Nxleave@2023"
                ) { result ->
                    result.onSuccess { auth ->
                        addStaff(auth)
                    }.onFailure {
                        setLoading(false)
                        viewModelScope.launch {
                            _uiEvent.emit(OnboardingUiEvent.GetStartFail("Fail in creating account!"))
                        }
                    }
                }
            } else {
                _uiEvent.emit(OnboardingUiEvent.GetStartFail("Fail in add role!"))
                setLoading(false)
            }
        }
    }

    private fun addStaff(auth: AuthUserModel) {
        viewModelScope.launch {
            val role = fireStoreRepository.getAllRoles().first()
            val success = fireStoreRepository.addStaff(
                StaffModel(
                    id = auth.id,
                    email = auth.email,
                    roleId = role.id,
                    name = "Admin",
                    phoneNumber = "",
                    currentProjectIds = emptyList(),
                    photo = "",
                    enable = true
                )
            )
            if (success) {
                setLoading(false)
                authRepository.updateStaffId(value = auth.id)
                _uiEvent.emit(OnboardingUiEvent.GetStartSuccess)
            } else {
                setLoading(false)
                _uiEvent.emit(OnboardingUiEvent.GetStartFail("Fail in add account"))
            }
        }
    }

    private fun setLoading(value: Boolean) {
        _uiState.update {
            it.copy(
                loading = value
            )
        }
    }
}

data class OnboardingUiState(
    val loading: Boolean = false,
    val isInitialized: Boolean = false
)

sealed interface OnboardingUiEvent {
    data object GetStartSuccess: OnboardingUiEvent
    data class GetStartFail(val message: String): OnboardingUiEvent
}