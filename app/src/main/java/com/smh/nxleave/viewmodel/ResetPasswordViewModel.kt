package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.repository.AuthRepository
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
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ResetPasswordUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun resetPassword(email: String) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.resetPassword(email) {
                setLoading(false)
                it
                    .onSuccess {
                        viewModelScope.launch {
                            _uiEvent.emit(ResetPasswordUiEvent.ResetSuccess)
                        }
                    }
                    .onFailure {
                        _uiState
                        viewModelScope.launch {
                            _uiEvent.emit(ResetPasswordUiEvent.Error(it.message.toString()))
                        }
                    }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        _uiState.update {
            it.copy(loading = loading)
        }
    }
}

data class ResetPasswordUiState(
    val loading: Boolean = false
)

sealed interface ResetPasswordUiEvent {
    data object ResetSuccess: ResetPasswordUiEvent
    data class Error(val message: String): ResetPasswordUiEvent
}