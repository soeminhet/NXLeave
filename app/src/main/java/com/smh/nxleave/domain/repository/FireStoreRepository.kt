package com.smh.nxleave.domain.repository

import com.google.firebase.firestore.ListenerRegistration
import com.smh.nxleave.design.component.LeaveStatus
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.domain.model.LeaveBalanceModel
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.screen.model.LeaveRequestModel
import java.time.OffsetDateTime

interface FireStoreRepository {
    suspend fun isInitialized(): Boolean

    suspend fun addStaff(model: StaffModel): Boolean
    suspend fun getAllStaff(): List<StaffModel>
    suspend fun updateStaff(model: StaffModel): Boolean

    suspend fun getAllRoles(): List<RoleModel>
    suspend fun getRole(uid: String): RoleModel?
    suspend fun addRole(model: RoleModel): Boolean
    suspend fun updateRole(model: RoleModel): Boolean

    suspend fun getAllLeaveTypes(): List<LeaveTypeModel>
    suspend fun addLeaveType(name: String, color: Long): Boolean
    suspend fun updateLeaveType(model: LeaveTypeModel): Boolean

    suspend fun getAllProjects(): List<ProjectModel>
    suspend fun addProject(model: ProjectModel, admins: List<StaffModel>): Boolean
    suspend fun updateProject(model: ProjectModel): Boolean

    suspend fun getAllLeaveBalance(): List<LeaveBalanceModel>
    suspend fun addLeaveBalance(model: LeaveBalanceModel): Boolean
    suspend fun updateLeaveBalance(model: LeaveBalanceModel): Boolean

    suspend fun getLeaveRequestBy(staffIds: List<String>, startDate: OffsetDateTime, endDate: OffsetDateTime): List<LeaveRequestModel>
    suspend fun addLeaveRequest(model: LeaveRequestModel): Boolean
    suspend fun deleteLeaveRequest(id: String): Boolean
    suspend fun updateLeaveRequestStatus(id: String, approverId: String, status: LeaveStatus): Boolean

    suspend fun addEvent(model: EventModel): Boolean
    suspend fun getAllEvents(): List<EventModel>
    suspend fun updateEvent(model: EventModel): Boolean
    suspend fun deleteEvent(id: String): Boolean
    suspend fun getAllUpcomingEvents(): List<EventModel>
}