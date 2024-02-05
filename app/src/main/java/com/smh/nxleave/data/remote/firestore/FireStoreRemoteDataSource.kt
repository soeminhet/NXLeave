package com.smh.nxleave.data.remote.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.smh.nxleave.design.component.LeaveStatus
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.domain.model.LeaveBalanceModel
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.screen.model.LeaveRequestModel
import java.time.OffsetDateTime

interface FireStoreRemoteDataSource {
    suspend fun isInitialized(): Boolean

    suspend fun addStaff(model: StaffModel): Boolean
    suspend fun getAllStaff(): List<DocumentSnapshot>
    suspend fun updateStaff(model: StaffModel): Boolean
    fun getRTStavesBy(projectIds: List<String>): Query
    fun getRTStaffBy(id: String): DocumentReference
    fun getRTAllStaves(): Query

    suspend fun getAllRoles(): List<DocumentSnapshot>
    suspend fun updateRole(model: RoleModel): Boolean
    suspend fun getRole(uid: String): DocumentSnapshot
    suspend fun addRole(model: RoleModel): Boolean
    fun getRTAllRoles(): Query

    suspend fun getAllLeaveTypes(): List<DocumentSnapshot>
    suspend fun addLeaveType(name: String, color: Long): Boolean
    suspend fun updateLeaveType(model: LeaveTypeModel): Boolean
    fun getRTAllLeaveTypes(): Query

    suspend fun getAllProjects(): List<DocumentSnapshot>
    suspend fun addProject(model: ProjectModel, admins: List<StaffModel>): Boolean
    suspend fun updateProject(model: ProjectModel): Boolean
    fun getRTAllProjects(): Query

    suspend fun getAllLeaveBalance(): List<DocumentSnapshot>
    suspend fun addLeaveBalance(model: LeaveBalanceModel): Boolean
    suspend fun updateLeaveBalance(model: LeaveBalanceModel): Boolean
    fun getRTLeaveBalanceBy(roleId: String): Query

    fun getRTLeaveRequestBy(staffId: String): Query
    fun getRTLeaveRequestBy(staffIds: List<String>): Query
    suspend fun getLeaveRequestBy(staffIds: List<String>, startDate: OffsetDateTime, endDate: OffsetDateTime): List<DocumentSnapshot>
    suspend fun addLeaveRequest(model: LeaveRequestModel): Boolean
    suspend fun deleteLeaveRequest(id: String): Boolean
    suspend fun updateLeaveRequestStatus(id: String, approverId: String, status: LeaveStatus): Boolean

    suspend fun addEvent(model: EventModel): Boolean
    suspend fun getAllEvents(): List<DocumentSnapshot>
    suspend fun updateEvent(model: EventModel): Boolean
    suspend fun deleteEvent(id: String): Boolean
    suspend fun getAllUpcomingEvents(): List<DocumentSnapshot>
    fun getRTAllUpcomingEvents(): Query
}