package com.smh.nxleave.domain.repository

import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.domain.model.LeaveBalanceModel
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.screen.model.LeaveRequestModel
import kotlinx.coroutines.flow.Flow

interface RealTimeDataRepository {
    fun getCurrentStaff(): Flow<StaffModel>
    fun getRelatedStaffBy(projectIds: List<String>): Flow<List<StaffModel>>
    fun getLeaveRequestBy(staffIds: List<String>): Flow<List<LeaveRequestModel>>
    fun getLeaveRequestBy(staffId: String): Flow<List<LeaveRequestModel>>
    fun getAllUpcomingEvent(): Flow<List<EventModel>>
    fun getAllLeaveTypes(): Flow<List<LeaveTypeModel>>
    fun getAllStaves(): Flow<List<StaffModel>>
    fun getAllRoles(): Flow<List<RoleModel>>
    fun getAllProjects(): Flow<List<ProjectModel>>
    fun getLeaveBalanceBy(roleId: String): Flow<List<LeaveBalanceModel>>
}