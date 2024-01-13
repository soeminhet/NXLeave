package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.domain.repository.FireStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpcomingEventsViewModel @Inject constructor(
    private val fireStoreRepository: FireStoreRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(UpcomingEventsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchAllUpcomingEvents()
    }

    private fun fetchAllUpcomingEvents() {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val events = fireStoreRepository.getAllUpcomingEvents()
            _uiState.update {
                it.copy(events = events)
            }
            setLoading(false)
        }
    }

    private fun setLoading(loading: Boolean) {
        _uiState.update {
            it.copy(loading = loading)
        }
    }
}

data class UpcomingEventsUiState(
    val loading: Boolean = false,
    val events: List<EventModel> = emptyList()
)