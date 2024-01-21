package com.smh.nxleave.domain.mapper

import com.smh.nxleave.design.component.LeaveStatus
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.screen.model.LeaveRequestModel
import com.smh.nxleave.screen.model.LeaveRequestUiModel
import com.smh.nxleave.utility.DATE_PATTERN_ONE
import com.smh.nxleave.utility.toDays
import java.time.format.DateTimeFormatter

fun List<LeaveRequestModel>.toUiModels(
    projects: List<ProjectModel>,
    staves: List<StaffModel>,
    leaveTypes: List<LeaveTypeModel>,
    roles: List<RoleModel>,
): List<LeaveRequestUiModel> {
    if (projects.isEmpty() || staves.isEmpty() || leaveTypes.isEmpty() || roles.isEmpty()) return emptyList()
    return this.map {
        it.toUiModel(
            staves = staves,
            leaveTypes = leaveTypes,
            roles = roles,
            projects = projects
        )
    }
}

private fun LeaveRequestModel.toUiModel(
    projects: List<ProjectModel>,
    staves: List<StaffModel>,
    leaveTypes: List<LeaveTypeModel>,
    roles: List<RoleModel>,
): LeaveRequestUiModel {
    val staff = staves.first { staff -> staff.id == staffId }

    val dateRange = if (period != null) {
        "${startDate.format(DATE_PATTERN_ONE)} $period"
    } else {
        "${startDate.format(DATE_PATTERN_ONE)} - ${endDate?.format(DATE_PATTERN_ONE)}"
    }

    return LeaveRequestUiModel(
        id = id,
        staffName = staff.name,
        role = roles.first { role -> role.id == staff.roleId },
        startDate = startDate.format(DATE_PATTERN_ONE),
        endDate = endDate?.format(DATE_PATTERN_ONE).orEmpty(),
        period = period.orEmpty(),
        applyDate = leaveApplyDate.format(DATE_PATTERN_ONE),
        approveDate = leaveApprovedDate?.format(DATE_PATTERN_ONE).orEmpty(),
        rejectDate = leaveRejectedDate?.format(DATE_PATTERN_ONE).orEmpty(),
        approver = staves.firstOrNull{ s -> s.id == approverId },
        currentProjects = staff.currentProjectIds.mapNotNull { id -> projects.firstOrNull { pj -> pj.id == id } },
        description = description,
        leaveType = leaveTypes.first { type -> type.id == leaveTypeId },
        dateRange = dateRange,
        duration = duration.toDays(),
        leaveStatus = LeaveStatus.entries.first { it.name == leaveStatus }
    )
}