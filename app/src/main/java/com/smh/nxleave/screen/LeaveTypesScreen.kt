package com.smh.nxleave.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.smh.nxleave.design.component.LeaveTypeItem
import com.smh.nxleave.design.component.NXAlertDialog
import com.smh.nxleave.design.component.NXBackButton
import com.smh.nxleave.design.component.NXFloatingButton
import com.smh.nxleave.design.component.NXLoading
import com.smh.nxleave.design.sheet.ManageLeaveTypeSheet
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.viewmodel.LeaveTypeUiEvent
import com.smh.nxleave.viewmodel.LeaveTypesUiState
import com.smh.nxleave.viewmodel.LeaveTypesViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LeaveTypesScreen(
    onBack: () -> Unit,
    viewModel: LeaveTypesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.loading) NXLoading()

    var showExistDialog by remember { mutableStateOf(false) }
    if (showExistDialog) {
        NXAlertDialog(
            title = "Sorry",
            body = "LeaveType already exist.",
            confirmButton = { showExistDialog = false }
        )
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvent.collectLatest {
            when(it) {
                LeaveTypeUiEvent.LeaveTypeExist -> showExistDialog = true
            }
        }
    }

    LeaveTypesContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                LeaveTypesUserEvent.OnBack -> onBack()
                is LeaveTypesUserEvent.OnAddLeaveType -> viewModel.addLeaveType(it.name, it.color)
                is LeaveTypesUserEvent.OnDisableLeaveType -> viewModel.updateLeaveType(it.model.copy(enable = false))
                is LeaveTypesUserEvent.OnUpdateLeaveType -> viewModel.updateLeaveType(it.model)
                is LeaveTypesUserEvent.OnEnableLeaveType -> viewModel.updateLeaveType(it.model.copy(enable = true))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun LeaveTypesContent(
    uiState: LeaveTypesUiState,
    userEvent: (LeaveTypesUserEvent) -> Unit
) {
    var showAddSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf<LeaveTypeModel?>(null) }
    var showDisableDialog by remember { mutableStateOf<LeaveTypeModel?>(null) }

    if (showAddSheet) {
        ManageLeaveTypeSheet(
            title = "ADD LEAVE TYPE",
            onSubmit = { value, color ->
                userEvent(LeaveTypesUserEvent.OnAddLeaveType(value, color))
                showAddSheet = false
            },
            leaveTypeModel = null,
            btnLabel = "SUBMIT",
            onDismissRequest = { showAddSheet = false }
        )
    }

    showEditSheet?.let { model ->
        ManageLeaveTypeSheet(
            title = "EDIT LEAVE TYPE",
            leaveTypeModel = model,
            onSubmit = { value, color ->
                userEvent(LeaveTypesUserEvent.OnUpdateLeaveType(model.copy(name = value, color = color)))
                showEditSheet = null
            },
            btnLabel = "UPDATE",
            onDismissRequest = { showEditSheet = null }
        )
    }

    showDisableDialog?.let { model ->
        NXAlertDialog(
            title = "Disable",
            body = "Are you sure want to disable ${model.name}?",
            confirmButton = {
                userEvent(LeaveTypesUserEvent.OnDisableLeaveType(model))
                showDisableDialog = null
            },
            dismissButton = { showDisableDialog = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "LeaveTypes") },
                navigationIcon = {
                    NXBackButton(onBack = { userEvent(LeaveTypesUserEvent.OnBack) })
                },
            )
        },
        floatingActionButton = {
            NXFloatingButton(
                onClick = { showAddSheet = true }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .animateContentSize()
        ) {
            items(
                uiState.leaveTypes,
                key = { model -> model.id }
            ) { model ->
                LeaveTypeItem(
                    model = model,
                    onEdit = { showEditSheet = it },
                    onDisable = { showDisableDialog = it },
                    onEnable = { userEvent(LeaveTypesUserEvent.OnEnableLeaveType(it)) },
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}

sealed interface LeaveTypesUserEvent {
    data object OnBack: LeaveTypesUserEvent
    data class OnAddLeaveType(val name: String, val color: Long): LeaveTypesUserEvent
    data class OnUpdateLeaveType(val model: LeaveTypeModel): LeaveTypesUserEvent
    data class OnDisableLeaveType(val model: LeaveTypeModel): LeaveTypesUserEvent
    data class OnEnableLeaveType(val model: LeaveTypeModel): LeaveTypesUserEvent
}

@Preview(showBackground = true)
@Composable
private fun LeaveTypesPreview() {
    NXLeaveTheme {
        LeaveTypesContent(
            uiState = LeaveTypesUiState(),
            userEvent = {}
        )
    }
}