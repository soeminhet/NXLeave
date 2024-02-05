package com.smh.nxleave.data.repository

import com.google.firebase.firestore.ListenerRegistration
import com.smh.nxleave.data.local.datastore.LocalDataStore
import com.smh.nxleave.data.mapper.toLeaveRequestModel
import com.smh.nxleave.data.mapper.toLeaveTypeModel
import com.smh.nxleave.data.mapper.toProjectModel
import com.smh.nxleave.data.mapper.toRoleModel
import com.smh.nxleave.data.mapper.toStaffModel
import com.smh.nxleave.data.remote.firestore.FireStoreRemoteDataSource
import com.smh.nxleave.domain.model.LeaveBalanceModel
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class RealTimeDataRepositoryImpl @Inject constructor(
    private val fireStoreRemoteDataSource: FireStoreRemoteDataSource,
    private val localDataStore: LocalDataStore,
): RealTimeDataRepository {
    private val _currentStaff: MutableStateFlow<StaffModel?> = MutableStateFlow(null)
    override val currentStaff: StateFlow<StaffModel?>
        get() = _currentStaff.asStateFlow()

    private val _projects: MutableStateFlow<List<ProjectModel>> = MutableStateFlow(emptyList())
    override val projects: StateFlow<List<ProjectModel>>
        get() = _projects.asStateFlow()

    private val _staves: MutableStateFlow<List<StaffModel>> = MutableStateFlow(emptyList())
    override val staves: StateFlow<List<StaffModel>>
        get() = _staves.asStateFlow()

    private val _roles: MutableStateFlow<List<RoleModel>> = MutableStateFlow(emptyList())
    override val roles: StateFlow<List<RoleModel>>
        get() = _roles.asStateFlow()

    private val _leaveTypes: MutableStateFlow<List<LeaveTypeModel>> = MutableStateFlow(emptyList())
    override val leaveTypes: StateFlow<List<LeaveTypeModel>>
        get() = _leaveTypes.asStateFlow()

    private var initJob: Job? = null
    private var projectListenerRegistration: ListenerRegistration? = null
    private var stavesListenerRegistration: ListenerRegistration? = null
    private var rolesListenerRegistration: ListenerRegistration? = null
    private var leaveTypesListenerRegistration: ListenerRegistration? = null
    private var currentStaffListenerRegistration: ListenerRegistration? = null
    private var relatedStavesListenerRegistration: ListenerRegistration? = null
    private var currentStaffLeaveBalanceListenerRegistration: ListenerRegistration? = null

    init {
        initJob = CoroutineScope(Dispatchers.IO).launch {
            launch {
                localDataStore.staffIdFlow
                    .distinctUntilChanged()
                    .collectLatest { id ->
                        if (id.isNotBlank()) {
                            listenCurrentStaff(id)
                            listenProjects()
                            listenStaves()
                            listenRoles()
                            listenLeaveTypes()
                        } else {
                            removeAllListeners()
                        }
                    }
            }
        }
    }

    private fun listenCurrentStaff(id: String) {
        currentStaffListenerRegistration?.remove()
        currentStaffListenerRegistration = fireStoreRemoteDataSource.getRTStaffBy(id = id)
            .addSnapshotListener { value, _ ->
                val staff = value?.data?.toStaffModel()
                _currentStaff.value = staff
            }
    }

    private fun listenProjects() {
        projectListenerRegistration?.remove()
        projectListenerRegistration = fireStoreRemoteDataSource.getRTAllProjects()
            .addSnapshotListener { value, _ ->
                _projects.value = value?.documents
                    ?.mapNotNull { it.data }
                    ?.map { it.toProjectModel() }
                    .orEmpty()
            }
    }

    private fun listenStaves() {
        stavesListenerRegistration?.remove()
        stavesListenerRegistration = fireStoreRemoteDataSource.getRTAllStaves()
            .addSnapshotListener { value, _ ->
                _staves.value = value?.documents
                    ?.mapNotNull { it.data }
                    ?.map { it.toStaffModel() }
                    .orEmpty()
            }
    }

    private fun listenRoles() {
        rolesListenerRegistration?.remove()
        rolesListenerRegistration = fireStoreRemoteDataSource.getRTAllRoles()
            .addSnapshotListener { value, _ ->
                _roles.value = value?.documents
                    ?.mapNotNull { it.data }
                    ?.map { it.toRoleModel() }
                    .orEmpty()
            }
    }

    private fun listenLeaveTypes() {
        leaveTypesListenerRegistration?.remove()
        leaveTypesListenerRegistration = fireStoreRemoteDataSource.getRTAllLeaveTypes()
            .addSnapshotListener { value, _ ->
                _leaveTypes.value = value?.documents
                    ?.mapNotNull { it.data }
                    ?.map { it.toLeaveTypeModel() }
                    .orEmpty()
            }
    }

    override fun onClear() {
        initJob?.cancel()
        removeAllListeners()
    }

    override fun removeAllListeners() {
        currentStaffListenerRegistration?.remove()
        projectListenerRegistration?.remove()
        stavesListenerRegistration?.remove()
        rolesListenerRegistration?.remove()
        leaveTypesListenerRegistration?.remove()
        relatedStavesListenerRegistration?.remove()
        currentStaffLeaveBalanceListenerRegistration?.remove()
    }
}