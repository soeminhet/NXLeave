package com.smh.nxleave.screen.bottomnav

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.nxleave.LocalEntryPadding
import com.smh.nxleave.design.component.LeaveStatus
import com.smh.nxleave.design.component.NXLoading
import com.smh.nxleave.design.component.ShowLeaveItem
import com.smh.nxleave.design.component.modifier.noRippleClick
import com.smh.nxleave.design.sheet.LeaveInfoSheet
import com.smh.nxleave.screen.model.LeaveRequestUiModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.viewmodel.LeaveApproveUiState
import com.smh.nxleave.viewmodel.LeaveApproveViewModel

@Composable
fun LeaveApproveScreen(
    viewModel: LeaveApproveViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LeaveApproveContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                is LeaveApproveUserEvent.OnApprove -> viewModel.updateLeaveStatus(it.id, LeaveStatus.Approved)
                is LeaveApproveUserEvent.OnReject -> viewModel.updateLeaveStatus(it.id, LeaveStatus.Rejected)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LeaveApproveContent(
    uiState: LeaveApproveUiState,
    userEvent: (LeaveApproveUserEvent) -> Unit
) {
    val tab = remember {
        val status = LeaveStatus.entries.map { it.name }
        val mStatus = status.toMutableList()
        mStatus.add(0, "All")
        mStatus.toList()
    }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var leaveInfoSheetData by remember { mutableStateOf<LeaveRequestUiModel?>(null) }

    val filteredLeaveRequests by remember(uiState.leaveRequests) {
        derivedStateOf {
            if (selectedTab == 0) uiState.leaveRequests
            else uiState.leaveRequests.filter { it.leaveStatus == LeaveStatus.entries[selectedTab - 1] }
        }
    }

    leaveInfoSheetData?.let {
        LeaveInfoSheet(
            uiModel = it,
            onDismiss = { leaveInfoSheetData = null },
            onApprove = { id ->
                userEvent(LeaveApproveUserEvent.OnApprove(id))
                leaveInfoSheetData = null
            },
            onReject = { id ->
                userEvent(LeaveApproveUserEvent.OnReject(id))
                leaveInfoSheetData = null
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .padding(LocalEntryPadding.current)
            .statusBarsPadding(),
        contentWindowInsets = WindowInsets(top = 0, bottom = 0),
        topBar = {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                tab.forEachIndexed { index, staus ->
                    Box(
                        modifier = Modifier
                            .noRippleClick { selectedTab = index }
                            .padding(top = spacing.space12)
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = staus,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .animateContentSize(),
            contentPadding = PaddingValues(spacing.horizontalSpace),
            verticalArrangement = Arrangement.spacedBy(spacing.space12)
        ) {
            items(filteredLeaveRequests) { model ->
                ShowLeaveItem(
                    role = model.role,
                    title = model.staffName,
                    dates = model.dateRange,
                    status = model.leaveStatus,
                    leaveType = model.leaveType,
                    modifier = Modifier
                        .animateItemPlacement()
                        .clip(CardDefaults.shape)
                        .clickable { leaveInfoSheetData = model }
                )
            }
        }
    }
}

sealed interface LeaveApproveUserEvent {
    data class OnApprove(val id: String): LeaveApproveUserEvent
    data class OnReject(val id: String): LeaveApproveUserEvent
}

@Preview(showBackground = true)
@Composable
private fun LeaveApprovePreview() {
    NXLeaveTheme {
        LeaveApproveContent(
            uiState = LeaveApproveUiState(),
            userEvent = {}
        )
    }
}