package com.smh.nxleave.screen.bottomnav

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.nxleave.LocalEntryPadding
import com.smh.nxleave.design.component.Label
import com.smh.nxleave.design.component.LeaveStatus
import com.smh.nxleave.design.component.LeaveType
import com.smh.nxleave.design.component.NXAlertDialog
import com.smh.nxleave.design.component.NXCircleProgress
import com.smh.nxleave.design.component.NXFloatingButton
import com.smh.nxleave.design.component.ShowLeaveItem
import com.smh.nxleave.design.sheet.LeaveRequestSheet
import com.smh.nxleave.design.sheet.MyLeaveBalanceSheet
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.screen.model.LeaveRequestModel
import com.smh.nxleave.screen.model.LeaveRequestUiModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.viewmodel.BalanceUiEvent
import com.smh.nxleave.viewmodel.BalanceUiState
import com.smh.nxleave.viewmodel.BalanceViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BalanceScreen(
    viewModel: BalanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var notEnoughLeaveDaysDialogData by remember { mutableStateOf<Pair<String, Double>?>(null) }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvent.collectLatest {
            when(it) {
                is BalanceUiEvent.NotEnoughLeave -> {
                    notEnoughLeaveDaysDialogData = Pair(it.leaveTypeName, it.leftDays)
                }
            }
        }
    }

    notEnoughLeaveDaysDialogData?.let {
        NXAlertDialog(
            body = "You have only ${it.second} day(s) left for ${it.first}",
            confirmButton = { notEnoughLeaveDaysDialogData = null }
        )
    }

    BalanceContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                is BalanceUserEvent.OnLeaveRequest -> viewModel.submitLeaveRequest(it.model)
                is BalanceUserEvent.OnDeleteRequest -> viewModel.deleteLeaveRequest(it.model)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun BalanceContent(
    uiState: BalanceUiState,
    userEvent: (BalanceUserEvent) -> Unit
) {
    var showMyLeaveBalances by remember { mutableStateOf(false) }
    var showRequestLeaveSheet by remember { mutableStateOf(false) }
    var cancelPendingLeaveRequestDialogData by remember { mutableStateOf<LeaveRequestUiModel?>(null) }

    if (showRequestLeaveSheet) {
        LeaveRequestSheet(
            leaveTypes = uiState.leaveTypes.filter { it.enable },
            onDismissRequest = { showRequestLeaveSheet = false},
            onSubmit = {
                userEvent(BalanceUserEvent.OnLeaveRequest(it))
                showRequestLeaveSheet = false
            }
        )
    }

    if (showMyLeaveBalances) {
        MyLeaveBalanceSheet(
            balances = uiState.myLeaveBalances,
            onDismissRequest = { showMyLeaveBalances = false }
        )
    }

    cancelPendingLeaveRequestDialogData?.let {
        NXAlertDialog(
            title = "Leave Cancel",
            body = "Do you want to cancel ${it.duration} ${it.leaveType.name}?",
            confirmButton = {
                userEvent(BalanceUserEvent.OnDeleteRequest(it))
                cancelPendingLeaveRequestDialogData = null
            },
            dismissButton = { cancelPendingLeaveRequestDialogData = null },
            dismissButtonText = "No",
            confirmButtonText = "Yes"
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(top = 0, bottom = 0),
        floatingActionButton = {
           NXFloatingButton(
               onClick = { showRequestLeaveSheet = true }
           )
        },
        modifier = Modifier
            .statusBarsPadding()
            .padding(LocalEntryPadding.current)
            .fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .animateContentSize()
        ) {
            item {
                Row(
                    modifier = Modifier
                        .animateItemPlacement()
                        .fillMaxWidth()
                        .padding(all = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    NXCircleProgress(
                        progresses = uiState.leaveTookPercentages,
                        centerContent = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 20.dp)
                            ) {
                                Text(
                                    text = "Total Left",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = uiState.leftDays,
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                TextButton(onClick = { showMyLeaveBalances = true }) {
                                    Text(
                                        text = "View Detail",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier
                                    )
                                }

                            }
                        },
                        modifier = Modifier.size(250.dp),
                        strokeWidth = 30f
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .animateItemPlacement()
                        .padding(horizontal = 20.dp)
                ) {
                    FlowRow(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp),
                    ) {
                        uiState.leaveTypes
                            .filter { type -> type.enable }
                            .forEach { type ->
                                LeaveType(
                                    color = Color(type.color),
                                    label = type.name,
                                )
                            }
                    }
                }
            }

            stickyHeader {
                Label(
                    value = "My Leaves History",
                    modifier = Modifier
                        .animateItemPlacement()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(
                            start = 20.dp,
                            end = 10.dp,
                            top = 10.dp
                        )
                )
            }

            items(
                uiState.leaveRequests,
                key = { request -> request.id }
            ) { model ->
                ShowLeaveItem(
                    modifier = Modifier
                        .animateItemPlacement()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 16.dp)
                        .clip(CardDefaults.shape)
                        .clickable(enabled = model.leaveStatus == LeaveStatus.Pending) {
                            cancelPendingLeaveRequestDialogData = model
                        },
                    status = model.leaveStatus,
                    title = model.duration,
                    dates = model.dateRange,
                    leaveType = model.leaveType
                )
            }
        }
    }
}

sealed interface BalanceUserEvent {
    data class OnLeaveRequest(val model: LeaveRequestModel): BalanceUserEvent
    data class OnDeleteRequest(val model: LeaveRequestUiModel): BalanceUserEvent
}

@Preview(showBackground = true)
@Composable
private fun BalanceScreenPreview() {
    NXLeaveTheme {
        BalanceContent(
            uiState = BalanceUiState(
                leaveTypes = listOf(
                    LeaveTypeModel.annualLeave,
                )
            ),
            userEvent = {}
        )
    }
}