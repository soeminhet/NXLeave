package com.smh.nxleave.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.nxleave.design.component.NXAlertDialog
import com.smh.nxleave.design.component.NXBackButton
import com.smh.nxleave.design.component.NXLoading
import com.smh.nxleave.design.component.ShowLeaveItem
import com.smh.nxleave.design.sheet.ReportFilterSheet
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.viewmodel.ReportUiState
import com.smh.nxleave.viewmodel.ReportViewModel
import java.time.OffsetDateTime

@Composable
fun ReportScreen(
    onBack: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.loading) NXLoading()

    ReportContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                ReportUserEvent.OnBack -> onBack()
                is ReportUserEvent.OnFilterApply -> {
                    viewModel.onFilterApply(it.staff, it.role, it.project, it.startDate, it.endDate)
                }
                ReportUserEvent.OnPrint -> {
                    viewModel.generateExcelAndPushLocalNotification(context)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ReportContent(
    uiState: ReportUiState,
    userEvent: (ReportUserEvent) -> Unit
) {
    val enableExcelReport by remember(uiState.leaveRequests) {
        derivedStateOf {
            uiState.leaveRequests.isNotEmpty()
        }
    }

    var showFilterSheet by remember { mutableStateOf(false) }
    if (showFilterSheet) {
        ReportFilterSheet(
            staves = uiState.staves,
            roles = uiState.roles,
            projects = uiState.projects,
            initSelectedStaff = uiState.selectedStaff,
            initSelectedRole = uiState.selectedRole,
            initSelectedProject = uiState.selectedProject,
            initSelectedStartDate = uiState.selectedStartDate,
            initSelectedEndDate = uiState.selectedEndDate,
            onDismissRequest = { showFilterSheet = false },
            onApply = { staff, role, project, startDate, endDate ->
                userEvent(ReportUserEvent.OnFilterApply(staff, role, project, startDate, endDate))
                showFilterSheet = false
            }
        )
    }

    var showExcelReport by remember { mutableStateOf(false) }
    if (showExcelReport) {
        NXAlertDialog(
            title = "EXCEL REPORT",
            body = "Do you want to generate Excel sheet?",
            confirmButton = {
                userEvent(ReportUserEvent.OnPrint)
                showExcelReport = false
            },
            onDismissRequest = {
                showExcelReport = false
            },
            dismissButton = {
                showExcelReport = false
            },
            dismissButtonText = "No",
            confirmButtonText = "Yes"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Report") },
                navigationIcon = {
                    NXBackButton(onBack = { userEvent(ReportUserEvent.OnBack) })
                },
                actions = {
                    IconButton(
                        onClick = { showExcelReport = true},
                        enabled = enableExcelReport
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Print"
                        )
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                }
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .animateContentSize()
                .padding(it)
                .fillMaxSize(),
            contentPadding = PaddingValues(all = spacing.horizontalSpace),
            verticalArrangement = Arrangement.spacedBy(spacing.space12)
        ) {
            items(
                uiState.leaveRequests,
                key = { request -> request.id }
            ) { request ->
                ShowLeaveItem(
                    status = request.leaveStatus,
                    title = request.staffName,
                    dates = request.dateRange,
                    leaveType = request.leaveType,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}

sealed interface ReportUserEvent {
    data object OnBack: ReportUserEvent
    data class OnFilterApply(
        val staff: StaffModel,
        val role: RoleModel,
        val project: ProjectModel,
        val startDate: OffsetDateTime,
        val endDate: OffsetDateTime
    ): ReportUserEvent
    data object OnPrint: ReportUserEvent
}

@Preview(showBackground = true)
@Composable
private fun ReportPreview() {
    NXLeaveTheme {
        ReportContent(
            uiState = ReportUiState(),
            userEvent = {}
        )
    }
}