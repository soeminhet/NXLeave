package com.smh.nxleave.screen

import NX_Charcoal_20
import NX_Charcoal_80
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.nxleave.design.component.NXBackButton
import com.smh.nxleave.design.component.NXLoading
import com.smh.nxleave.design.sheet.EditLeaveBalanceSheet
import com.smh.nxleave.screen.model.LeaveBalanceUiModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.viewmodel.LeaveBalanceUiState
import com.smh.nxleave.viewmodel.LeaveBalanceViewModel

@Composable
fun LeaveBalanceScreen(
    onBack: () -> Unit,
    viewModel: LeaveBalanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.loading) NXLoading()

    LeaveBalanceContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                LeaveBalanceUserEvent.OnBack -> onBack()
                is LeaveBalanceUserEvent.OnUpdateLeaveBalances -> viewModel.updateLeaveBalances(it.list)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaveBalanceContent(
    uiState: LeaveBalanceUiState,
    userEvent: (LeaveBalanceUserEvent) -> Unit,
) {
    var editLeaveBalanceSheet by remember {
        mutableStateOf<Pair<String, List<LeaveBalanceUiModel>>?>(null)
    }

    editLeaveBalanceSheet?.let {
        EditLeaveBalanceSheet(
            title = it.first,
            leaveBalanceList = it.second,
            onDismissRequest = { editLeaveBalanceSheet = null },
            onSubmit = { list ->
                userEvent(LeaveBalanceUserEvent.OnUpdateLeaveBalances(list))
                editLeaveBalanceSheet = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "LeaveBalance") },
                navigationIcon = {
                    NXBackButton(onBack = { userEvent(LeaveBalanceUserEvent.OnBack) })
                },
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(
                horizontal = spacing.horizontalSpace,
                vertical = spacing.space12
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.space12)
        ) {
            items(
                uiState.leaveBalanceMap.toList(),
                key = { it.first }
            ) {
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = spacing.space12),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.first,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = spacing.space12)
                            )

                            TextButton(
                                onClick = { editLeaveBalanceSheet = it },
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(spacing.space8)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(text = "Edit")
                                }
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(spacing.space12)
                        ) {
                            Divider(color = NX_Charcoal_20)

                            it.second.forEach { uiModel ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = spacing.space12),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = uiModel.leaveTypeName,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${uiModel.balance} Days",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed interface LeaveBalanceUserEvent {
    data object OnBack: LeaveBalanceUserEvent
    data class OnUpdateLeaveBalances(val list: List<LeaveBalanceUiModel>): LeaveBalanceUserEvent
}

@Preview(showBackground = true)
@Composable
private fun LeaveBalancePreview() {
    NXLeaveTheme {
        LeaveBalanceContent(
            uiState = LeaveBalanceUiState(),
            userEvent = {}
        )
    }
}