package com.smh.nxleave.design.sheet

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.smh.nxleave.design.component.NXOutlinedTextField
import com.smh.nxleave.design.component.SheetLip
import com.smh.nxleave.design.component.modifier.noRippleClick
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.utility.DATE_PATTERN_ONE
import com.smh.nxleave.utility.getCurrentMonthStartAndEndOffsetDate
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFilterSheet(
    initSelectedStaff: StaffModel,
    initSelectedRole: RoleModel,
    initSelectedProject: ProjectModel,
    initSelectedStartDate: OffsetDateTime,
    initSelectedEndDate: OffsetDateTime,
    staves: List<StaffModel>,
    roles: List<RoleModel>,
    projects: List<ProjectModel>,
    onDismissRequest: () -> Unit,
    onApply: (StaffModel, RoleModel, ProjectModel, OffsetDateTime, OffsetDateTime) -> Unit
) {
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0),
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        ReportFilterContent(
            initSelectedStaff = initSelectedStaff,
            initSelectedRole = initSelectedRole,
            initSelectedProject = initSelectedProject,
            initSelectedStartDate = initSelectedStartDate,
            initSelectedEndDate = initSelectedEndDate,
            staves = staves,
            roles = roles,
            projects = projects,
            onApply = onApply
        )
    }
}

@Composable
private fun ReportFilterContent(
    initSelectedStaff: StaffModel,
    initSelectedRole: RoleModel,
    initSelectedProject: ProjectModel,
    initSelectedStartDate: OffsetDateTime,
    initSelectedEndDate: OffsetDateTime,
    staves: List<StaffModel>,
    roles: List<RoleModel>,
    projects: List<ProjectModel>,
    onApply: (StaffModel, RoleModel, ProjectModel, OffsetDateTime, OffsetDateTime) -> Unit
) {
    var showStavesSheet by remember { mutableStateOf(false) }
    var showRolesSheet by remember { mutableStateOf(false) }
    var showProjectsSheet by remember { mutableStateOf(false) }
    var showStartDateSheet by remember { mutableStateOf(false) }
    var showEndDateSheet by remember { mutableStateOf(false) }
    var staff by remember { mutableStateOf(initSelectedStaff) }
    var role by remember { mutableStateOf(initSelectedRole) }
    var project by remember { mutableStateOf(initSelectedProject) }
    var startDate by remember { mutableStateOf(initSelectedStartDate) }
    var endDate by remember { mutableStateOf(initSelectedEndDate) }

    val applyEnable by remember {
        derivedStateOf {
            staff != initSelectedStaff || role != initSelectedRole || project != initSelectedProject
                    || startDate != initSelectedStartDate || endDate != initSelectedEndDate
        }
    }
    val showRoleAndProject by remember {
        derivedStateOf {
            staff.id.isBlank()
        }
    }

    if (showStavesSheet) {
        SearchSingleItemSelectableSheet(
            items = staves,
            selectedItem = staff,
            onClick = {
                staff = it
                showStavesSheet = false
            },
            onDismissRequest = { showStavesSheet = false },
            onFilter = { search, list -> list.filter { it.name.contains(search, ignoreCase = true) }},
            itemContent = {
                Text(
                    it.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = spacing.space12)
                )
            }
        )
    }

    if (showRolesSheet) {
        SearchSingleItemSelectableSheet(
            items = roles,
            selectedItem = role,
            onClick = {
                role = it
                showRolesSheet = false
            },
            onDismissRequest = { showRolesSheet = false },
            onFilter = { search, list -> list.filter { it.name.contains(search, ignoreCase = true) }},
            itemContent = {
                Text(
                    it.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = spacing.space12)
                )
            }
        )
    }

    if (showProjectsSheet) {
        SearchSingleItemSelectableSheet(
            items = projects,
            selectedItem = project,
            onClick = {
                project = it
                showProjectsSheet = false
            },
            onDismissRequest = { showProjectsSheet = false },
            onFilter = { search, list -> list.filter { it.name.contains(search, ignoreCase = true) }},
            itemContent = {
                Text(
                    it.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = spacing.space12)
                )
            }
        )
    }

    if (showStartDateSheet) {
        NXDatePickerSheet(
            selectedDate = startDate.toInstant().toEpochMilli(),
            onSelectedDate = {
                startDate = it
                showStartDateSheet = false
            },
            onDismissRequest = {
                showStartDateSheet = false
            },
            title = "Select Start Date"
        )
    }

    if (showEndDateSheet) {
        NXDatePickerSheet(
            selectedDate = endDate.toInstant().toEpochMilli(),
            onSelectedDate = {
                endDate = it
                showEndDateSheet = false
            },
            onDismissRequest = {
                showEndDateSheet = false
            },
            title = "Select End Date"
        )
    }

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = spacing.horizontalSpace)
            .padding(bottom = spacing.sheetBottomSpace)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(spacing.space12)
    ) {
        SheetLip()

        NXOutlinedTextField(
            value = staff.name,
            onValueChange = {},
            label = "Staff",
            readOnly = true,
            enabled = false,
            topLabel = true,
            modifier = Modifier
                .noRippleClick { showStavesSheet = true }
                .padding(top = spacing.space12),
            colors = OutlinedTextFieldDefaults.colors(
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledTextColor = MaterialTheme.colorScheme.onSurface
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = ""
                )
            }
        )

        if (showRoleAndProject) {
            NXOutlinedTextField(
                value = role.name,
                onValueChange = {},
                label = "Role",
                readOnly = true,
                enabled = false,
                topLabel = true,
                modifier = Modifier
                    .noRippleClick { showRolesSheet = true }
                    .padding(top = spacing.space12),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = ""
                    )
                }
            )

            NXOutlinedTextField(
                value = project.name,
                onValueChange = {},
                label = "Project",
                readOnly = true,
                enabled = false,
                topLabel = true,
                modifier = Modifier
                    .noRippleClick { showProjectsSheet = true }
                    .padding(top = spacing.space12),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = ""
                    )
                }
            )
        }

        NXOutlinedTextField(
            value = startDate.format(DATE_PATTERN_ONE),
            onValueChange = {},
            label = "StartDate",
            readOnly = true,
            enabled = false,
            topLabel = true,
            modifier = Modifier
                .noRippleClick { showStartDateSheet = true }
                .padding(top = spacing.space12),
            colors = OutlinedTextFieldDefaults.colors(
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledTextColor = MaterialTheme.colorScheme.onSurface
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = ""
                )
            }
        )

        NXOutlinedTextField(
            value = endDate.format(DATE_PATTERN_ONE),
            onValueChange = {},
            label = "EndDate",
            readOnly = true,
            enabled = false,
            topLabel = true,
            modifier = Modifier
                .noRippleClick { showEndDateSheet = true }
                .padding(top = spacing.space12),
            colors = OutlinedTextFieldDefaults.colors(
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledTextColor = MaterialTheme.colorScheme.onSurface
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = ""
                )
            }
        )

        Button(
            onClick = {
                onApply(staff, role, project, startDate, endDate)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = spacing.space6),
            enabled = applyEnable
        ) {
            Text(text = "APPLY")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportFilterPreview() {
    NXLeaveTheme {
        val currentMonth = getCurrentMonthStartAndEndOffsetDate()
        ReportFilterContent(
            initSelectedStaff = StaffModel.allStaff,
            initSelectedProject = ProjectModel.allProject,
            initSelectedRole = RoleModel.allRole,
            initSelectedStartDate = currentMonth.first,
            initSelectedEndDate = currentMonth.second,
            staves = listOf(StaffModel.projectManager),
            roles = listOf(RoleModel.allRole),
            projects = listOf(ProjectModel.allProject),
            onApply = { _, _, _, _, _ -> }
        )
    }
}