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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun loginAccount(email: String, password: String) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.signIn(email, password) {
                it
                    .onSuccess { auth ->
                        setLoading(false)
                        viewModelScope.launch {
                            authRepository.updateStaffId(value = auth.id)
                        }
                    }
                    .onFailure {
                        setLoading(false)
                        viewModelScope.launch {
                            _uiEvent.emit(LoginUiEvent.Error(it.message.toString()))
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

data class LoginUiState(
    val loading: Boolean = false,
)

sealed interface LoginUiEvent {
    data class Error(val message: String) : LoginUiEvent
}