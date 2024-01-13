package com.smh.nxleave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.repository.FireStoreRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import com.smh.nxleave.domain.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val realTimeDataRepository: RealTimeDataRepository,
    private val fireStoreRepository: FireStoreRepository,
    private val storageRepository: StorageRepository,
): ViewModel() {
    private var profileImageFile: File? = null

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<EditProfileUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            realTimeDataRepository.currentStaff
                .collectLatest { model ->
                    if (model != null) {
                        _uiState.update {
                            it.copy(
                                photo = model.photo,
                                name = model.name,
                                phoneNumber = model.phoneNumber
                            )
                        }
                    }
                }
        }
    }

    fun onProfileImageChange(value: File) {
        profileImageFile = value
    }

    fun onNameChange(value: String) {
        _uiState.update {
            it.copy(name = value)
        }
    }

    fun onPhoneNumberChange(value: String) {
        _uiState.update {
            it.copy(phoneNumber = value)
        }
    }

    fun updateInfo() {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val photoURL = async {
                if (profileImageFile == null) ""
                else { storageRepository.uploadImage(profileImageFile!!) }
            }.await()
            val updatedModel = realTimeDataRepository.currentStaff.value!!.copy(
                photo = photoURL.ifBlank { uiState.value.photo },
                name = uiState.value.name,
                phoneNumber = uiState.value.phoneNumber
            )
            val success = fireStoreRepository.updateStaff(updatedModel)
            if (success) {
                _uiEvent.emit(EditProfileUiEvent.SubmitSuccess)
            } else {
                _uiEvent.emit(EditProfileUiEvent.SubmitError)
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

data class EditProfileUiState(
    val loading: Boolean = false,
    val name: String = "",
    val phoneNumber: String = "",
    val photo: String = ""
) {
    val submitEnable: Boolean get() = phoneNumber.isNotBlank() && name.isNotBlank()
}

sealed interface EditProfileUiEvent {
    data object SubmitSuccess: EditProfileUiEvent
    data object SubmitError: EditProfileUiEvent
}