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
import com.smh.nxleave.design.component.ProjectItem
import com.smh.nxleave.design.sheet.Option
import com.smh.nxleave.design.sheet.OptionsContent
import com.smh.nxleave.design.sheet.ProjectManagementContent
import com.smh.nxleave.design.sheet.ProjectManagementSheet
import com.smh.nxleave.design.sheet.SheetPreview
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.viewmodel.ProjectsUiEvent
import com.smh.nxleave.viewmodel.ProjectsUiState
import com.smh.nxleave.viewmodel.ProjectsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProjectsScreen(
    onBack: () -> Unit,
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.loading) NXLoading()

    var showExistDialog by remember { mutableStateOf(false) }
    if (showExistDialog) {
        NXAlertDialog(
            title = "Sorry",
            body = "Project name already exist.",
            confirmButton = { showExistDialog = false }
        )
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvent.collectLatest {
            when(it) {
                ProjectsUiEvent.ProjectExist -> showExistDialog = true
            }
        }
    }

    ProjectsContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                ProjectsUserEvent.OnBack -> onBack()
                is ProjectsUserEvent.OnAddProject -> viewModel.addProject(it.model)
                is ProjectsUserEvent.OnUpdateProject -> viewModel.updateProject(it.model)
                is ProjectsUserEvent.OnDisableProject -> viewModel.updateProjectEnable(it.model.copy(enable = false))
                is ProjectsUserEvent.OnEnableProject -> viewModel.updateProjectEnable(it.model.copy(enable = true))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ProjectsContent(
    uiState: ProjectsUiState,
    userEvent: (ProjectsUserEvent) -> Unit
) {
    var showAddSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf<ProjectModel?>(null) }
    var showDisableDialog by remember { mutableStateOf<ProjectModel?>(null) }

    if (showAddSheet) {
        ProjectManagementSheet(
            onSubmit = {
                userEvent(ProjectsUserEvent.OnAddProject(it))
                showAddSheet = false
            },
            onDismissRequest = {
                showAddSheet = false
            }
        )
    }

    showEditSheet?.let { model ->
        ProjectManagementSheet(
            model = model,
            onSubmit = {
                userEvent(ProjectsUserEvent.OnUpdateProject(it))
                showEditSheet = null
            },
            onDismissRequest = { showEditSheet = null }
        )
    }

    showDisableDialog?.let { model ->
        NXAlertDialog(
            title = "Disable",
            body = "Are you sure want to disable ${model.name}?",
            confirmButton = {
                userEvent(ProjectsUserEvent.OnDisableProject(model))
                showDisableDialog = null
            },
            dismissButton = { showDisableDialog = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Projects") },
                navigationIcon = {
                    NXBackButton(onBack = { userEvent(ProjectsUserEvent.OnBack) })
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
                uiState.projects,
                key = { model -> model.id }
            ) { model ->
                ProjectItem(
                    model = model,
                    onEdit = { showEditSheet = it },
                    onDisable = { showDisableDialog = it },
                    onEnable = { userEvent(ProjectsUserEvent.OnEnableProject(model)) },
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}

sealed interface ProjectsUserEvent {
    data object OnBack: ProjectsUserEvent
    data class OnAddProject(val model: ProjectModel): ProjectsUserEvent
    data class OnUpdateProject(val model: ProjectModel): ProjectsUserEvent
    data class OnEnableProject(val model: ProjectModel): ProjectsUserEvent
    data class OnDisableProject(val model: ProjectModel): ProjectsUserEvent
}

@Preview
@Composable
private fun ProjectsPreview() {
    NXLeaveTheme {
        SheetPreview(
            content = {
                ProjectsContent(
                    uiState = ProjectsUiState()
                        .copy(
                            projects = listOf(
                                ProjectModel.example,
                                ProjectModel.example2
                            )
                        ),
                    userEvent = {}
                )
            },
            sheet = {
                OptionsContent(
                    options = listOf(Option.EDIT, Option.DISABLE),
                    onCancel = { /*TODO*/ },
                    onClick = {}
                )
            }
        )
    }
}