package com.smh.nxleave.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.nxleave.design.component.NXBackButton
import com.smh.nxleave.design.component.NXLoading
import com.smh.nxleave.design.component.NXOutlinedTextField
import com.smh.nxleave.design.component.NXProfile
import com.smh.nxleave.design.component.NXTitleAndSlogan
import com.smh.nxleave.design.sheet.SingleItemSelectableSheet
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.viewmodel.EditProfileUiEvent
import com.smh.nxleave.viewmodel.EditProfileUiState
import com.smh.nxleave.viewmodel.EditProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvent.collectLatest {
            when(it) {
                EditProfileUiEvent.SubmitError -> {  }
                EditProfileUiEvent.SubmitSuccess -> { onBack() }
            }
        }
    }

    if (uiState.loading) NXLoading()

    EditProfileContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                EditProfileUserEvent.OnBack -> onBack()
                EditProfileUserEvent.OnSubmit -> viewModel.updateInfo()
                is EditProfileUserEvent.OnNameChange -> viewModel.onNameChange(it.value)
                is EditProfileUserEvent.OnPhoneChange -> viewModel.onPhoneNumberChange(it.value)
                is EditProfileUserEvent.OnProfileChange -> viewModel.onProfileImageChange(it.value)
            }
        }
    )
}

@Composable
private fun EditProfileContent(
    uiState: EditProfileUiState,
    userEvent: (EditProfileUserEvent) -> Unit
) {
    Scaffold(
        topBar = {
            NXBackButton(
                onBack = { userEvent(EditProfileUserEvent.OnBack) },
                modifier = Modifier
                    .padding(start = spacing.space10)
                    .statusBarsPadding()
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = spacing.horizontalSpace),
            verticalArrangement = Arrangement.spacedBy(spacing.space32)
        ) {
            NXTitleAndSlogan(
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NXProfile(
                        url = uiState.photo,
                        onUpdateProfileImage = { image ->
                            userEvent(EditProfileUserEvent.OnProfileChange(image))
                        }
                    )
                }

                NXOutlinedTextField(
                    value = uiState.name,
                    onValueChange = { value -> userEvent(EditProfileUserEvent.OnNameChange(value)) },
                    label = "Name",
                    modifier = Modifier.padding(top = spacing.space24),
                )

                NXOutlinedTextField(
                    value = uiState.phoneNumber,
                    onValueChange = { value -> userEvent(EditProfileUserEvent.OnPhoneChange(value)) },
                    label = "PhoneNumber",
                    modifier = Modifier.padding(top = spacing.space12),
                )

                Button(
                    onClick = { userEvent(EditProfileUserEvent.OnSubmit) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.space20),
                    enabled = uiState.submitEnable
                ) {
                    Text(text = "UPDATE")
                }
            }
        }
    }
}

sealed interface EditProfileUserEvent {
    data object OnBack: EditProfileUserEvent
    data object OnSubmit: EditProfileUserEvent
    data class OnProfileChange(val value: File): EditProfileUserEvent
    data class OnNameChange(val value: String): EditProfileUserEvent
    data class OnPhoneChange(val value: String): EditProfileUserEvent
}

@Preview(showBackground = true)
@Composable
private fun EditProfileProfilePreview() {
    NXLeaveTheme {
        EditProfileContent(
            uiState = EditProfileUiState(),
            userEvent = {}
        )
    }
}