package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.model.EventModel
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
class EventManagementViewModel @Inject constructor(
    private val fireStoreRepository: FireStoreRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(EventManagementUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<EventManagementUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val events = fireStoreRepository.getAllEvents()
            _uiState.update {
                it.copy(
                    events = events
                )
            }
            setLoading(false)
        }
    }

    fun addEvent(model: EventModel) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            if (checkExist(model.name)) {
                _uiEvent.emit(EventManagementUiEvent.EventExist)
                setLoading(false)
                return@launch
            }
            val success = fireStoreRepository.addEvent(model)
            if (!success) {
                setLoading(false)
                _uiEvent.emit(EventManagementUiEvent.AddEventError)
            }
            fetchEvents()
        }
    }

    fun updateEvent(model: EventModel) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            if (checkExist(model.name)) {
                _uiEvent.emit(EventManagementUiEvent.EventExist)
                setLoading(false)
                return@launch
            }
            val success = fireStoreRepository.updateEvent(model)
            if (!success) {
                setLoading(false)
                _uiEvent.emit(EventManagementUiEvent.UpdateEventError)
            }
            fetchEvents()
        }
    }

    fun deleteEvent(model: EventModel) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val success = fireStoreRepository.deleteEvent(model.id)
            if (!success) {
                setLoading(false)
                _uiEvent.emit(EventManagementUiEvent.DeleteEventError)
            }
            fetchEvents()
        }
    }

    private fun checkExist(name: String): Boolean {
        val trimmed = name.removeWhiteSpaces()
        return uiState.value.events.any { event ->
            event.name.removeWhiteSpaces().equals(trimmed, ignoreCase = true)
        }
    }

    private fun setLoading(loading: Boolean) {
        _uiState.update {
            it.copy(
                loading = loading
            )
        }
    }
}

data class EventManagementUiState(
    val loading: Boolean = false,
    val events: List<EventModel> = emptyList()
)

sealed interface EventManagementUiEvent {
    data object AddEventError: EventManagementUiEvent
    data object UpdateEventError: EventManagementUiEvent
    data object DeleteEventError: EventManagementUiEvent
    data object EventExist: EventManagementUiEvent
}