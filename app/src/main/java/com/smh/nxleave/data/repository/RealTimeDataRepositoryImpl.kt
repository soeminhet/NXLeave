package com.smh.nxleave.data.repository

import com.google.firebase.firestore.ListenerRegistration
import com.smh.nxleave.data.local.datastore.LocalDataStore
import com.smh.nxleave.data.mapper.toEventModel
import com.smh.nxleave.data.mapper.toLeaveRequestModel
import com.smh.nxleave.data.mapper.toLeaveTypeModel
import com.smh.nxleave.data.mapper.toProjectModel
import com.smh.nxleave.data.mapper.toRoleModel
import com.smh.nxleave.data.mapper.toStaffModel
import com.smh.nxleave.data.remote.firestore.FireStoreRemoteDataSource
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.domain.model.LeaveBalanceModel
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import com.smh.nxleave.screen.model.LeaveRequestModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import javax.inject.Inject

class RealTimeDataRepositoryImpl @Inject constructor(
    private val fireStoreRemoteDataSource: FireStoreRemoteDataSource,
    private val localDataStore: LocalDataStore,
): RealTimeDataRepository {
    override fun getCurrentStaff(): Flow<StaffModel> {
        return callbackFlow {
            var listener: ListenerRegistration? = null
            localDataStore.staffIdFlow
                .filterNot { it.isEmpty() }
                .collectLatest {
                    listener?.remove()
                    listener = fireStoreRemoteDataSource.getRTStaffBy(it).addSnapshotListener { value, _ ->
                        value?.data?.toStaffModel()?.let { staff ->
                            launch {
                                send(staff)
                            }
                        }
                    }
                }

            awaitClose { listener?.remove() }
        }
    }

    override fun getRelatedStaffBy(projectIds: List<String>): Flow<List<StaffModel>> {
        return callbackFlow {
            val listener = fireStoreRemoteDataSource.getRTStavesBy(projectIds)
                .addSnapshotListener { value, error ->
                    launch {
                        send(
                            value?.documents
                                ?.mapNotNull { it.data }
                                ?.map { it.toStaffModel() }
                                .orEmpty()
                        )
                    }
                }

            awaitClose{ listener.remove() }
        }
    }

    override fun getLeaveRequestBy(staffIds: List<String>): Flow<List<LeaveRequestModel>> {
        return callbackFlow {
            val listener = fireStoreRemoteDataSource.getRTLeaveRequestBy(staffIds)
                .addSnapshotListener { value, error ->
                    launch {
                        send(
                            value
                                ?.documents
                                ?.map { document -> document.toLeaveRequestModel() }
                                .orEmpty()
                        )
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    override fun getLeaveRequestBy(staffId: String): Flow<List<LeaveRequestModel>> {
        return callbackFlow {
            val listener = fireStoreRemoteDataSource.getRTLeaveRequestBy(staffId)
                .addSnapshotListener { value, _ ->
                    launch {
                        send(
                            value
                                ?.documents
                                ?.map { document -> document.toLeaveRequestModel() }
                                .orEmpty()
                        )
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    override fun getAllUpcomingEvent(): Flow<List<EventModel>> {
        return callbackFlow {
            val listener = fireStoreRemoteDataSource.getRTAllUpcomingEvents()
                .limit(10)
                .addSnapshotListener { value, _ ->
                    launch {
                        send(
                            value
                                ?.documents
                                ?.map { document -> document.toEventModel() }
                                .orEmpty()
                        )
                    }
                }
            awaitClose{ listener.remove() }
        }
    }

    override fun getAllLeaveTypes(): Flow<List<LeaveTypeModel>> {
        return callbackFlow {
            val listener = fireStoreRemoteDataSource.getRTAllLeaveTypes()
                .addSnapshotListener { value, _ ->
                    launch {
                        send(
                            value?.documents
                                ?.mapNotNull { it.data }
                                ?.map { it.toLeaveTypeModel() }
                                .orEmpty()
                        )
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    override fun getAllStaves(): Flow<List<StaffModel>> {
        return callbackFlow {
            val listener = fireStoreRemoteDataSource.getRTAllStaves()
                .addSnapshotListener { value, _ ->
                    launch {
                        send(
                            value?.documents
                                ?.mapNotNull { it.data }
                                ?.map { it.toStaffModel() }
                                .orEmpty()
                        )
                    }
                }
            awaitClose{ listener.remove() }
        }
    }

    override fun getAllRoles(): Flow<List<RoleModel>> {
        return callbackFlow {
            val listener = fireStoreRemoteDataSource.getRTAllRoles()
                .addSnapshotListener { value, _ ->
                    launch {
                        send(
                            value?.documents
                                ?.mapNotNull { it.data }
                                ?.map { it.toRoleModel() }
                                .orEmpty()
                        )
                    }
                }

            awaitClose{ listener.remove() }
        }
    }

    override fun getAllProjects(): Flow<List<ProjectModel>> {
        return callbackFlow {
            val listener = fireStoreRemoteDataSource.getRTAllProjects()
                .addSnapshotListener { value, _ ->
                    launch {
                        send(
                            value?.documents
                                ?.mapNotNull { it.data }
                                ?.map { it.toProjectModel() }
                                .orEmpty()
                        )
                    }
                }

            awaitClose{ listener.remove() }
        }
    }

    override fun getLeaveBalanceBy(roleId: String): Flow<List<LeaveBalanceModel>> {
        return callbackFlow {
            val listener = fireStoreRemoteDataSource.getRTLeaveBalanceBy(roleId)
                .addSnapshotListener { value, _ ->
                    launch {
                        send(
                            value?.documents
                                ?.mapNotNull { it.data }
                                ?.map { it.toLeaveRequestModel() }
                                .orEmpty()
                        )
                    }
                }
            awaitClose { listener.remove() }
        }
    }
}