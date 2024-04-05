package com.smh.nxleave.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.nxleave.design.component.NXAlertDialog
import com.smh.nxleave.design.component.NXBackButton
import com.smh.nxleave.design.component.NXFloatingButton
import com.smh.nxleave.design.component.NXLoading
import com.smh.nxleave.design.component.StaffManagementItem
import com.smh.nxleave.design.sheet.ManageStaffContent
import com.smh.nxleave.design.sheet.ManageStaffSheet
import com.smh.nxleave.design.sheet.Option
import com.smh.nxleave.design.sheet.OptionsContent
import com.smh.nxleave.design.sheet.SheetPreview
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.screen.model.StaffProfileUiModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.viewmodel.RoleUiEvent
import com.smh.nxleave.viewmodel.StaffManagementUiEvent
import com.smh.nxleave.viewmodel.StaffManagementUiState
import com.smh.nxleave.viewmodel.StaffManagementViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun StaffManagementScreen(
    onBack: () -> Unit,
    viewModel: StaffManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.loading) NXLoading()

    var showExistDialog by remember { mutableStateOf(false) }
    if (showExistDialog) {
        NXAlertDialog(
            title = "Sorry",
            body = "Staff name already exist.",
            confirmButton = { showExistDialog = false }
        )
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvent.collectLatest {
            when(it) {
                StaffManagementUiEvent.AccountCreateError -> {}
                StaffManagementUiEvent.AccountExist -> showExistDialog = true
                StaffManagementUiEvent.SaveStaffError -> {}
                StaffManagementUiEvent.UpdateStaffError -> {}
            }
        }
    }

    StaffManagementContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                StaffManagementUserEvent.OnBack -> onBack()
                is StaffManagementUserEvent.OnManageStaff -> viewModel.mangeAccount(it.model, it.password)
                is StaffManagementUserEvent.OnDisableStaff -> viewModel.updateStaffEnable(it.model.copy(enable = false))
                is StaffManagementUserEvent.OnEnableStaff -> viewModel.updateStaffEnable(it.model.copy(enable = true))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StaffManagementContent(
    uiState: StaffManagementUiState,
    userEvent: (StaffManagementUserEvent) -> Unit
) {
    var showManageSheet by remember { mutableStateOf(false) }
    var manageSheetData by remember { mutableStateOf<StaffProfileUiModel?>(null) }
    var showDisableDialog by remember { mutableStateOf<StaffProfileUiModel?>(null) }

    if (showManageSheet) {
        ManageStaffSheet(
            roles = uiState.enableRoles,
            projects = uiState.enableProjects,
            onCreate = { staffModel, password ->
                userEvent(StaffManagementUserEvent.OnManageStaff(staffModel, password))
                showManageSheet = false
            },
            onDismissRequest = { showManageSheet = false}
        )
    }

    manageSheetData?.let {
        ManageStaffSheet(
            staffModel = it,
            roles = uiState.enableRoles,
            projects = uiState.enableProjects,
            onCreate = { staffModel, password ->
                userEvent(StaffManagementUserEvent.OnManageStaff(staffModel, password))
                manageSheetData = null
            },
            onDismissRequest = { manageSheetData = null }
        )
    }

    showDisableDialog?.let { model ->
        NXAlertDialog(
            title = "Disable",
            body = "Are you sure want to disable ${model.name}?",
            confirmButton = {
                userEvent(StaffManagementUserEvent.OnDisableStaff(model))
                showDisableDialog = null
            },
            dismissButton = { showDisableDialog = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Staves") },
                navigationIcon = {
                    NXBackButton(onBack = { userEvent(StaffManagementUserEvent.OnBack) })
                },
            )
        },
        floatingActionButton = {
            NXFloatingButton(
                onClick = { showManageSheet = true },
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(vertical = spacing.space12),
            verticalArrangement = Arrangement.spacedBy(spacing.space12)
        ) {
            items(
                uiState.staves,
                key = { it.id }
            ) { staff ->
                StaffManagementItem(
                    staff = staff,
                    onEdit = { staffProfile -> manageSheetData = staffProfile },
                    onEnable = { userEvent(StaffManagementUserEvent.OnEnableStaff(it)) },
                    onDisable = { showDisableDialog = it }
                )
            }
        }
    }
}

sealed interface StaffManagementUserEvent {
    data object OnBack: StaffManagementUserEvent
    data class OnManageStaff(val model: StaffModel, val password: String): StaffManagementUserEvent
    data class OnEnableStaff(val model: StaffProfileUiModel): StaffManagementUserEvent
    data class OnDisableStaff(val model: StaffProfileUiModel): StaffManagementUserEvent
}

@Preview(showBackground = true)
@Composable
private fun StaffManagementPreview() {
    NXLeaveTheme {
        SheetPreview(
            content = {
                StaffManagementContent(
                    uiState = StaffManagementUiState(
                        staves = listOf(
                            StaffProfileUiModel("1", "", "Admin", "admin@nxleave.co", "", "", "", emptyList(), true),
                            StaffProfileUiModel("2", "", "Staff One", "staffone@nxleave.co", "", "", "", emptyList(), true),
                            StaffProfileUiModel("3", "", "PM One", "pmone@nxleave.co", "", "", "", emptyList(), true)
                        )
                    ),
                    userEvent = {}
                )
            },
            sheet = {
                OptionsContent(
                    options = listOf(Option.EDIT, Option.DISABLE),
                    onCancel = {},
                    onClick = {}
                )
            }
        )
    }
}