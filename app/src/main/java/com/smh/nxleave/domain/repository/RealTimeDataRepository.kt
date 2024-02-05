package com.smh.nxleave.domain.repository

import com.smh.nxleave.domain.model.LeaveBalanceModel
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RealTimeDataRepository {
    val currentStaff: StateFlow<StaffModel?>
    val currentStaffLeaveBalance: StateFlow<List<LeaveBalanceModel>>
    val relatedStaves: StateFlow<List<StaffModel>>
    val projects: StateFlow<List<ProjectModel>>
    val staves: StateFlow<List<StaffModel>>
    val roles: StateFlow<List<RoleModel>>
    val leaveTypes: StateFlow<List<LeaveTypeModel>>

    fun removeAllListeners()
    fun onClear()
}

interface RealTimeDataRepositoryV2 {
    fun currentStaff(): Flow<StaffModel>
}