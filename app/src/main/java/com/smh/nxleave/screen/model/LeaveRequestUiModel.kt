package com.smh.nxleave.screen.model

import com.smh.nxleave.design.component.LeaveStatus
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel

data class LeaveRequestUiModel(
    val id: String,
    val staffName: String,
    val role: RoleModel,
    val startDate: String,
    val endDate: String,
    val period: String,
    val applyDate: String,
    val approveDate: String,
    val rejectDate: String,
    val approver: StaffModel?,
    val currentProjects: List<ProjectModel>,
    val leaveType: LeaveTypeModel,
    val description: String,
    val dateRange: String,
    val duration: String,
    val leaveStatus: LeaveStatus,
) {
    companion object {
        val examplePending = LeaveRequestUiModel(
            id = "1",
            staffName = "Soe Min Htet",
            role = RoleModel.androidDeveloper,
            startDate = "12 Dec 2023",
            endDate = "15 Dec 2023",
            period = "",
            applyDate = "12, Dec 2023",
            approveDate = "",
            rejectDate = "",
            approver = null,
            currentProjects = listOf(ProjectModel.example),
            leaveType = LeaveTypeModel.annualLeave,
            description = "I want to take 3 day annual leave cause of my personal matter",
            dateRange = "12 Dec 2023 - 15 Dec 2023",
            duration = "3 Days",
            leaveStatus = LeaveStatus.Pending
        )

        val exampleApprove = LeaveRequestUiModel(
            id = "2",
            staffName = "Revee",
            role = RoleModel.projectManager,
            startDate = "12 Dec 2023",
            endDate = "12 Dec 2023",
            period = "AM",
            applyDate = "12, Dec 2023",
            approveDate = "",
            rejectDate = "",
            approver = null,
            currentProjects = listOf(ProjectModel.example),
            leaveType = LeaveTypeModel.medicalLeave,
            description = "I want to take 3 day annual leave cause of my personal matter",
            dateRange = "12 Dec 2023 AM",
            duration = "0.5 Day",
            leaveStatus = LeaveStatus.Approved
        )

        val exampleRejected = LeaveRequestUiModel(
            id = "3",
            staffName = "Alex",
            role = RoleModel.iOSDeveloper,
            startDate = "12 Dec 2023",
            endDate = "15 Dec 2023",
            period = "",
            applyDate = "12, Dec 2023",
            approveDate = "",
            rejectDate = "",
            approver = null,
            currentProjects = listOf(ProjectModel.example),
            leaveType = LeaveTypeModel.annualLeave,
            description = "I want to take 3 day annual leave cause of my personal matter",
            dateRange = "12 Dec 2023 - 15 Dec 2023",
            duration = "3 Days",
            leaveStatus = LeaveStatus.Rejected
        )
    }
}
