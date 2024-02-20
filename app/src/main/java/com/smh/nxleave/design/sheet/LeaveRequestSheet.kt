package com.smh.nxleave.design.sheet

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.design.component.LeaveStatus
import com.smh.nxleave.design.component.NXOutlinedTextField
import com.smh.nxleave.design.component.modifier.noRippleClick
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.screen.model.LeaveRequestModel
import com.smh.nxleave.screen.model.PeriodModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveRequestSheet(
    leaveTypes: List<LeaveTypeModel>,
    onDismissRequest: () -> Unit,
    onSubmit: (LeaveRequestModel) -> Unit
) {
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0.dp),
        dragHandle = null,
        tonalElevation = 0.dp,
    ) {
        LeaveRequestContent(
            onDismiss = onDismissRequest,
            leaveTypes = leaveTypes,
            onSubmit = onSubmit
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaveRequestContent(
    onDismiss: () -> Unit,
    leaveTypes: List<LeaveTypeModel>,
    onSubmit: (LeaveRequestModel) -> Unit
) {
    var selectedLeaveType by remember { mutableStateOf<LeaveTypeModel?>(null) }
    var isHalfDayLeave by remember { mutableStateOf(false) }
    var selectedPeriod by remember { mutableStateOf<PeriodModel?>(null) }
    var startDate by remember { mutableStateOf<OffsetDateTime?>(null) }
    var endDate by remember { mutableStateOf<OffsetDateTime?>(null) }
    var description by remember { mutableStateOf("") }
    var leaveRequestDatePickerState by remember { mutableStateOf(LeaveRequestDatePickerState.Idle) }
    var showLeaveTypeSheet by remember { mutableStateOf(false) }
    var showPeriodSheet by remember { mutableStateOf(false) }

    val endDateError by remember {
        derivedStateOf {
            if (isHalfDayLeave) false
            else {
                if(startDate != null && endDate != null) {
                    startDate!!.toEpochSecond() > endDate!!.toEpochSecond()
                } else {
                    false
                }
            }
        }
    }
    val submitEnable by remember {
        derivedStateOf {
            selectedLeaveType != null && startDate != null &&
                    if (isHalfDayLeave) selectedPeriod != null else endDate != null && !endDateError
        }
    }

    if (leaveRequestDatePickerState != LeaveRequestDatePickerState.Idle) {
        val selectedDate = when (leaveRequestDatePickerState) {
            LeaveRequestDatePickerState.Idle -> null
            LeaveRequestDatePickerState.StartDate -> startDate?.toInstant()?.toEpochMilli()
            LeaveRequestDatePickerState.EndDate -> endDate?.toInstant()?.toEpochMilli()
        }
        NXDatePickerSheet(
            selectedDate = selectedDate,
            onSelectedDate = {
                when (leaveRequestDatePickerState) {
                    LeaveRequestDatePickerState.Idle -> {}
                    LeaveRequestDatePickerState.StartDate -> {
                        startDate = it
                    }

                    LeaveRequestDatePickerState.EndDate -> {
                        endDate = it
                    }
                }
                if (selectedDate == null || selectedDate != it.toInstant().toEpochMilli()) {
                    leaveRequestDatePickerState = LeaveRequestDatePickerState.Idle
                }
            },
            dateValidator = { timestamp ->
                timestamp > Instant.now().minusSeconds(86400).toEpochMilli()
            },
            onDismissRequest = { leaveRequestDatePickerState = LeaveRequestDatePickerState.Idle },
            title = when (leaveRequestDatePickerState) {
                LeaveRequestDatePickerState.Idle -> ""
                LeaveRequestDatePickerState.StartDate -> "Select Start Date"
                LeaveRequestDatePickerState.EndDate -> "Select End Date"
            }
        )
    }

    if (showLeaveTypeSheet) {
        SingleItemSelectableSheet(
            items = leaveTypes,
            selectedItem = selectedLeaveType,
            onClick = {
                selectedLeaveType = it
                showLeaveTypeSheet = false
            },
            onDismissRequest = { showLeaveTypeSheet = false },
            itemContent = {
                Text(
                    it.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = spacing.space16)
                )
            }
        )
    }

    if (showPeriodSheet) {
        SingleItemSelectableSheet(
            items = PeriodModel.periods,
            selectedItem = selectedPeriod,
            onClick = {
                selectedPeriod = it
                showPeriodSheet = false
            },
            onDismissRequest = { showPeriodSheet = false },
            itemContent = {
                Text(
                    it.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = spacing.space16)
                )
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Leave Request") },
                actions = {
                    IconButton(
                        onClick = onDismiss,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val duration = if (isHalfDayLeave) 0.5
                    else (Duration.between(startDate, endDate).toDays() + 1).toDouble()

                    val leaveEndDate = if(isHalfDayLeave) startDate else endDate!!
                    val requestModel = LeaveRequestModel(
                        id = "",
                        staffId = "",
                        leaveTypeId = selectedLeaveType!!.id,
                        duration = duration,
                        startDate = startDate!!,
                        endDate = leaveEndDate,
                        description = description,
                        leaveStatus = LeaveStatus.Pending.name,
                        leaveApplyDate = OffsetDateTime.now(),
                        leaveApprovedDate = null,
                        leaveRejectedDate = null,
                        period = if(isHalfDayLeave) selectedPeriod?.name else null,
                        approverId = ""
                    )
                    onSubmit(requestModel)
                },
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 30.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(percent = 10),
                enabled = submitEnable
            ) {
                Text(text = "SUBMIT")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .fillMaxSize()
                .padding(it)
                .verticalScroll(state = rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NXOutlinedTextField(
                value = selectedLeaveType?.name ?: "",
                label = "Leave Type",
                onValueChange = {},
                topLabel = true,
                readOnly = true,
                enabled = false,
                modifier = Modifier
                    .noRippleClick { showLeaveTypeSheet = true },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            NXOutlinedTextField(
                label = "Start Date",
                value = startDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "",
                onValueChange = {},
                topLabel = true,
                readOnly = true,
                enabled = false,
                modifier = Modifier
                    .noRippleClick {
                        leaveRequestDatePickerState = LeaveRequestDatePickerState.StartDate
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.offset(x = (-12).dp)
            ) {
                Checkbox(
                    checked = isHalfDayLeave,
                    onCheckedChange = { isHalfDayLeave = !isHalfDayLeave },
                    modifier = Modifier.height(30.dp)
                )

                Text(text = "Half day leave")
            }

            if (isHalfDayLeave) {
                NXOutlinedTextField(
                    label = "AM/PM",
                    value = selectedPeriod?.name ?: "",
                    onValueChange = {},
                    topLabel = true,
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier
                        .noRippleClick {
                            showPeriodSheet = true
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            if (!isHalfDayLeave) {
                NXOutlinedTextField(
                    label = "End Date",
                    value = endDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "",
                    onValueChange = {},
                    topLabel = true,
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier
                        .noRippleClick {
                            leaveRequestDatePickerState = LeaveRequestDatePickerState.EndDate
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    errorMsg = if (endDateError) "End Date must be after start date." else ""
                )
            }

            Column {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                NXOutlinedTextField(
                    value = description,
                    onValueChange = { value -> description = value },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                )
            }
        }
    }
}

private enum class LeaveRequestDatePickerState {
    Idle, StartDate, EndDate
}

@Preview
@Composable
private fun LeaveRequestContentPreview() {
    NXLeaveTheme {
        LeaveRequestContent(
            onDismiss = {},
            leaveTypes = emptyList(),
            onSubmit = {}
        )
    }
}