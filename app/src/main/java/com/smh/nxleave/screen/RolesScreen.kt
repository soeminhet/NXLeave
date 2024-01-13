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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.nxleave.design.component.EditableLabelItem
import com.smh.nxleave.design.component.NXAlertDialog
import com.smh.nxleave.design.component.NXBackButton
import com.smh.nxleave.design.component.NXFloatingButton
import com.smh.nxleave.design.component.NXLoading
import com.smh.nxleave.design.sheet.RoleManagementSheet
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.viewmodel.RolesUiState
import com.smh.nxleave.viewmodel.RolesViewModel

@Composable
fun RolesScreen(
    onBack: () -> Unit,
    viewModel: RolesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.loading) NXLoading()

    RolesContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                RolesUserEvent.OnBack -> onBack()
                is RolesUserEvent.OnAddRole -> viewModel.addRole(it.model)
                is RolesUserEvent.OnUpdateRole -> viewModel.updateRole(it.model)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun RolesContent(
    uiState: RolesUiState,
    userEvent: (RolesUserEvent) -> Unit
) {
    var showAddSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf<RoleModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf<RoleModel?>(null) }

    if (showAddSheet) {
        RoleManagementSheet(
            onSubmit = {
                userEvent(RolesUserEvent.OnAddRole(it))
                showAddSheet = false
            },
            onDismissRequest = { showAddSheet = false }
        )
    }

    showEditSheet?.let { model ->
        RoleManagementSheet(
            model = model,
            onSubmit = {
                userEvent(RolesUserEvent.OnUpdateRole(it))
                showEditSheet = null
            },
            onDismissRequest = { showEditSheet = null }
        )
    }

    showDeleteDialog?.let { model ->
        NXAlertDialog(
            title = "Disable",
            body = "Are you sure want to disable ${model.name}?",
            confirmButton = {
                userEvent(RolesUserEvent.OnUpdateRole(model.copy(enable = false)))
                showDeleteDialog = null
            },
            dismissButton = { showDeleteDialog = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Roles") },
                navigationIcon = {
                    NXBackButton(onBack = { userEvent(RolesUserEvent.OnBack) })
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
                uiState.roles,
                key = { model -> model.id }
            ) { model ->
                EditableLabelItem(
                    model = model,
                    enable = model.enable,
                    label = model.name,
                    onEdit = { showEditSheet = it },
                    onDisable = { showDeleteDialog = it },
                    onEnable = { userEvent(RolesUserEvent.OnUpdateRole(model.copy(enable = true))) },
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}

sealed interface RolesUserEvent {
    data object OnBack: RolesUserEvent
    data class OnAddRole(val model: RoleModel): RolesUserEvent
    data class OnUpdateRole(val model: RoleModel): RolesUserEvent
}

@Preview(showBackground = true)
@Composable
private fun RolesPreview() {
    NXLeaveTheme {
        RolesContent(
            uiState = RolesUiState(),
            userEvent = {}
        )
    }
}