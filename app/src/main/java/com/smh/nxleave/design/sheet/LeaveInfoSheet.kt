package com.smh.nxleave.design.sheet

import NX_Green
import NX_Red
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.design.component.InfoColumn
import com.smh.nxleave.design.component.LeaveStatus
import com.smh.nxleave.design.component.SheetLip
import com.smh.nxleave.screen.model.LeaveRequestUiModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveInfoSheet(
    uiModel: LeaveRequestUiModel,
    showActions: Boolean,
    onDismiss: () -> Unit,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
) {
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0.dp),
        dragHandle = null,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxHeight(fraction = spacing.sheetMaxFraction)
    ) {
        LeaveInfoSheetContent(
            uiModel = uiModel,
            showActions = showActions,
            onCancel = onDismiss,
            onApprove = onApprove,
            onReject = onReject
        )
    }
}

@Composable
private fun LeaveInfoSheetContent(
    uiModel: LeaveRequestUiModel,
    showActions: Boolean,
    onCancel: () -> Unit,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
) {
    Scaffold(
        bottomBar = {
            Column {
                Divider()

                Column(
                    modifier = Modifier
                        .padding(horizontal = spacing.horizontalSpace)
                        .padding(
                            top = spacing.space16,
                            bottom = spacing.sheetBottomSpace
                        )
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(spacing.space12)
                ) {
                    if (showActions) {
                        Button(
                            onClick = { onApprove(uiModel.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NX_Green,
                            )
                        ) {
                            Text(text = "APPROVE")
                        }

                        Button(
                            onClick = { onReject(uiModel.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NX_Red,
                            )
                        ) {
                            Text(text = "REJECT")
                        }

                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            Text(text = "CANCEL")
                        }
                    } else {
                        Button(
                            onClick = onCancel,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "CANCEL")
                        }
                    }
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = spacing.horizontalSpace),
            verticalArrangement = Arrangement.spacedBy(spacing.space12),
            contentPadding = PaddingValues(bottom = spacing.space12)
        ) {
            item {
                SheetLip()
            }

            item {
                InfoColumn(label = "Name", value = uiModel.staffName)
            }

            item {
                InfoColumn(label = "Role", value = uiModel.role.name)
            }

            item {
                InfoColumn(label = "StartDate", value = uiModel.startDate)
            }

            if (uiModel.endDate.isNotBlank()) {
                item {
                    InfoColumn(label = "End Date", value = uiModel.endDate)
                }
            }

            if (uiModel.period.isNotBlank()) {
                item {
                    InfoColumn(label = "Period", value = uiModel.period)
                }
            }

            item {
                InfoColumn(label = "Apply Date", value = uiModel.applyDate)
            }

            if (uiModel.approveDate.isNotBlank()) {
                item {
                    InfoColumn(label = "Approve Date", value = uiModel.applyDate)
                }
            }

            if (uiModel.rejectDate.isNotBlank()) {
                item {
                    InfoColumn(label = "Reject Date", value = uiModel.rejectDate)
                }
            }

            if (uiModel.approver != null) {
                item {
                    InfoColumn(label = "Approver", value = uiModel.approver.name)
                }
            }

            item {
                InfoColumn(label = "Current Projects", value = uiModel.currentProjects.joinToString { it.name })
            }

            item {
                InfoColumn(label = "Leave Type", value = uiModel.leaveType.name)
            }

            item {
                InfoColumn(label = "Leave Staus", value = uiModel.leaveStatus.name)
            }

            item {
                InfoColumn(
                    label = "Description",
                    value = uiModel.description.ifBlank { "-" }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LeaveInfoSheetContentPreview() {
    NXLeaveTheme {
        LeaveInfoSheetContent(
            uiModel = LeaveRequestUiModel.examplePending,
            showActions = true,
            onCancel = {},
            onApprove = {},
            onReject = {}
        )
    }
}