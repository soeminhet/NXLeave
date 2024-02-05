package com.smh.nxleave.data.repository

import com.google.firebase.firestore.ListenerRegistration
import com.smh.nxleave.data.mapper.toEventModel
import com.smh.nxleave.data.mapper.toLeaveRequestModel
import com.smh.nxleave.data.mapper.toLeaveTypeModel
import com.smh.nxleave.data.mapper.toProjectModel
import com.smh.nxleave.data.mapper.toRoleModel
import com.smh.nxleave.data.mapper.toStaffModel
import com.smh.nxleave.data.remote.firestore.FireStoreRemoteDataSource
import com.smh.nxleave.design.component.LeaveStatus
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.domain.model.LeaveBalanceModel
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.domain.repository.FireStoreRepository
import com.smh.nxleave.screen.model.LeaveRequestModel
import java.time.OffsetDateTime
import javax.inject.Inject

class FireStoreRepositoryImpl @Inject constructor(
    private val fireStoreRemoteDataSource: FireStoreRemoteDataSource
): FireStoreRepository {
    override suspend fun isInitialized(): Boolean {
        return fireStoreRemoteDataSource.isInitialized()
    }

    override suspend fun addStaff(model: StaffModel): Boolean {
        return try {
            fireStoreRemoteDataSource.addStaff(model)
        } catch(e: Exception) {
            false
        }
    }

    override suspend fun getAllStaff(): List<StaffModel> {
        return try {
            fireStoreRemoteDataSource.getAllStaff()
                .mapNotNull { it.data }
                .map {
                    it.toStaffModel()
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun updateStaff(model: StaffModel): Boolean {
        return try {
            fireStoreRemoteDataSource.updateStaff(model)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getAllRoles(): List<RoleModel> {
        return try {
            fireStoreRemoteDataSource.getAllRoles()
                .mapNotNull { it.data }
                .map {
                    it.toRoleModel()
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getRole(uid: String): RoleModel? {
        return try {
            val data = fireStoreRemoteDataSource.getRole(uid).data
            data?.toRoleModel()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addRole(model: RoleModel): Boolean {
        return try {
            fireStoreRemoteDataSource.addRole(model)
        } catch(e: Exception) {
            false
        }
    }

    override suspend fun updateRole(model: RoleModel): Boolean {
        return try {
            fireStoreRemoteDataSource.updateRole(model)
        } catch(e: Exception) {
            false
        }
    }

    override suspend fun getAllLeaveTypes(): List<LeaveTypeModel> {
        return try {
            fireStoreRemoteDataSource.getAllLeaveTypes()
                .mapNotNull { it.data }
                .map {
                    it.toLeaveTypeModel()
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addLeaveType(name: String, color: Long): Boolean {
        return try {
            fireStoreRemoteDataSource.addLeaveType(name, color)
        } catch(e: Exception) {
            false
        }
    }

    override suspend fun updateLeaveType(model: LeaveTypeModel): Boolean {
        return try {
            fireStoreRemoteDataSource.updateLeaveType(model)
        } catch(e: Exception) {
            false
        }
    }

    override suspend fun getAllProjects(): List<ProjectModel> {
        return try {
            fireStoreRemoteDataSource.getAllProjects()
                .mapNotNull { it.data }
                .map {
                    it.toProjectModel()
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addProject(model: ProjectModel, admins: List<StaffModel>): Boolean {
        return try {
            fireStoreRemoteDataSource.addProject(model, admins)
        } catch(e: Exception) {
            false
        }
    }

    override suspend fun updateProject(model: ProjectModel): Boolean {
        return try {
            fireStoreRemoteDataSource.updateProject(model)
        } catch(e: Exception) {
            false
        }
    }

    override suspend fun getAllLeaveBalance(): List<LeaveBalanceModel> {
        return try {
            fireStoreRemoteDataSource.getAllLeaveBalance()
                .mapNotNull { it.data }
                .map {
                    it.toLeaveRequestModel()
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addLeaveBalance(model: LeaveBalanceModel): Boolean {
        return try {
            fireStoreRemoteDataSource.addLeaveBalance(model)
        } catch(e: Exception) {
            false
        }
    }

    override suspend fun updateLeaveBalance(model: LeaveBalanceModel): Boolean {
        return try {
            fireStoreRemoteDataSource.updateLeaveBalance(model)
        } catch(e: Exception) {
            false
        }
    }

    override suspend fun getLeaveRequestBy(
        staffIds: List<String>,
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): List<LeaveRequestModel> {
        return fireStoreRemoteDataSource.getLeaveRequestBy(
            staffIds = staffIds,
            startDate = startDate,
            endDate = endDate
        ).map { it.toLeaveRequestModel() }
    }

    override suspend fun addLeaveRequest(model: LeaveRequestModel): Boolean {
        return try {
            fireStoreRemoteDataSource.addLeaveRequest(model)
        } catch(e: Exception) {
            false
        }
    }

    override suspend fun deleteLeaveRequest(id: String): Boolean {
        return try {
            fireStoreRemoteDataSource.deleteLeaveRequest(id)
        } catch(e: Exception) {
            false
        }
    }

    override suspend fun updateLeaveRequestStatus(id: String, approverId: String, status: LeaveStatus): Boolean {
        return fireStoreRemoteDataSource.updateLeaveRequestStatus(id, approverId, status)
    }

    override suspend fun addEvent(model: EventModel): Boolean {
        return try {
            fireStoreRemoteDataSource.addEvent(model)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getAllEvents(): List<EventModel> {
        return try {
            fireStoreRemoteDataSource.getAllEvents()
                .map { it.toEventModel() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun updateEvent(model: EventModel): Boolean {
        return try {
            fireStoreRemoteDataSource.updateEvent(model)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteEvent(id: String): Boolean {
        return try {
            fireStoreRemoteDataSource.deleteEvent(id)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getAllUpcomingEvents(): List<EventModel> {
        return try {
            fireStoreRemoteDataSource.getAllUpcomingEvents()
                .map { it.toEventModel() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}