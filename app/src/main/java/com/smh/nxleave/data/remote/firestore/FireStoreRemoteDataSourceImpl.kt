package com.smh.nxleave.data.remote.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smh.nxleave.data.mapper.toFireStoreMap
import com.smh.nxleave.design.component.LeaveStatus
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.domain.model.LeaveBalanceModel
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.screen.model.LeaveRequestModel
import com.smh.nxleave.utility.getTodayStartTimeStamp
import com.smh.nxleave.utility.toTimeStamp
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FireStoreRemoteDataSourceImpl @Inject constructor(): FireStoreRemoteDataSource {

    private val db = Firebase.firestore
    private val staffCollection = db.collection("Staff")
    private val roleCollection = db.collection("Role")
    private val leaveTypeCollection = db.collection("LeaveType")
    private val projectCollection = db.collection("Project")
    private val leaveBalanceCollection = db.collection("LeaveBalance")
    private val leaveRequestCollection = db.collection("LeaveRequest")
    private val eventCollection = db.collection("Event")

    override suspend fun isInitialized(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            staffCollection.get()
                .addOnSuccessListener {
                    continuation.resume(!it.isEmpty)
                }
                .addOnFailureListener {
                    continuation.resume(false)
                }
        }
    }

    override suspend fun addStaff(model: StaffModel): Boolean {
        return suspendCancellableCoroutine { continuation ->
            staffCollection.document(model.id)
                .set(model.toFireStoreMap())
                .addOnSuccessListener { continuation.resume(true) }
                .addOnFailureListener { continuation.resume(false) }
        }
    }

    override suspend fun getAllStaff(): List<DocumentSnapshot> {
        return suspendCancellableCoroutine { continuation ->
            staffCollection.get()
                .addOnSuccessListener {
                    continuation.resume(it.documents.toList())
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun updateStaff(model: StaffModel): Boolean {
        return suspendCancellableCoroutine { continuation ->
            staffCollection.document(model.id)
                .set(model.toFireStoreMap())
                .addContinuationBooleanListener(continuation)
        }
    }

    override fun getRTStavesBy(projectIds: List<String>): Query = staffCollection
        .whereArrayContainsAny("currentProjectIds", projectIds)

    override fun getRTStaffBy(id: String): DocumentReference = staffCollection.document(id)

    override fun getRTAllStaves(): Query = staffCollection

    override suspend fun getAllRoles(): List<DocumentSnapshot> {
        return suspendCancellableCoroutine { continuation ->
            roleCollection.get()
                .addOnSuccessListener {
                    continuation.resume(it.documents.toList())
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun getRole(uid: String): DocumentSnapshot {
        return suspendCancellableCoroutine { continuation ->
            roleCollection.document(uid).get()
                .addContinuationListener(continuation)
        }
    }

    override suspend fun addRole(model: RoleModel): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val document = roleCollection.document()
            document.set(model.copy(id = document.id).toFireStoreMap())
                .addContinuationBooleanListener(continuation)
        }
    }

    override suspend fun updateRole(model: RoleModel): Boolean {
        return suspendCancellableCoroutine { continuation ->
            roleCollection.document(model.id)
                .update(model.toFireStoreMap())
                .addContinuationBooleanListener(continuation)
        }
    }

    override fun getRTAllRoles(): Query = roleCollection

    override suspend fun getAllLeaveTypes(): List<DocumentSnapshot> {
        return suspendCancellableCoroutine { continuation ->
            leaveTypeCollection
                .orderBy("color")
                .get()
                .addOnSuccessListener { continuation.resume(it.documents.toList()) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    override suspend fun addLeaveType(name: String, color: Long): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val document = leaveTypeCollection.document()
            val model = LeaveTypeModel(
                id = document.id,
                name = name,
                color = color,
                enable = true
            )
            document.set(model)
                .addContinuationBooleanListener(continuation)
        }
    }

    override suspend fun updateLeaveType(model: LeaveTypeModel): Boolean {
        return suspendCancellableCoroutine { continuation ->
            leaveTypeCollection.document(model.id)
                .update(model.toFireStoreMap())
                .addContinuationBooleanListener(continuation)
        }
    }

    override fun getRTAllLeaveTypes(): Query = leaveTypeCollection

    override suspend fun getAllProjects(): List<DocumentSnapshot> {
        return suspendCancellableCoroutine { continuation ->
            projectCollection.get()
                .addOnSuccessListener { continuation.resume(it.documents.toList()) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    override suspend fun addProject(model: ProjectModel, admins: List<StaffModel>): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val document = projectCollection.document()
            document.set(model.copy(id = document.id))
                .addOnSuccessListener {
                    for (admin in admins) {
                        val mList = admin.currentProjectIds.toMutableList()
                        mList.add(document.id)
                        staffCollection.document(admin.id)
                            .update("currentProjectIds", mList.toList())
                    }
                    continuation.resume(true)
                }
                .addOnFailureListener { continuation.resume(false) }
        }
    }

    override suspend fun updateProject(model: ProjectModel): Boolean {
        return suspendCancellableCoroutine { continuation ->
            projectCollection.document(model.id)
                .update(model.toFireStoreMap())
                .addContinuationBooleanListener(continuation)
        }
    }

    override fun getRTAllProjects(): Query = projectCollection

    override suspend fun getAllLeaveBalance(): List<DocumentSnapshot> {
        return suspendCancellableCoroutine { continuation ->
            leaveBalanceCollection.get()
                .addOnSuccessListener { continuation.resume(it.documents.toList()) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    override suspend fun addLeaveBalance(model: LeaveBalanceModel): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val document = leaveBalanceCollection.document()
            document.set(model.copy(id = document.id))
                .addContinuationBooleanListener(continuation)
        }
    }

    override suspend fun updateLeaveBalance(model: LeaveBalanceModel): Boolean {
        return suspendCancellableCoroutine { continuation ->
            leaveBalanceCollection.document(model.id)
                .update(model.toFireStoreMap())
                .addContinuationBooleanListener(continuation)
        }
    }

    override fun getRTLeaveBalanceBy(roleId: String): Query = leaveBalanceCollection.whereEqualTo("roleId", roleId)

    override fun getRTLeaveRequestBy(staffId: String): Query {
        return leaveRequestCollection
            .whereEqualTo("staffId", staffId)
            .orderBy("leaveApplyDate", Query.Direction.DESCENDING)
    }

    override fun getRTLeaveRequestBy(staffIds: List<String>): Query {
        return leaveRequestCollection
            .whereIn("staffId", staffIds)
            .orderBy("endDate")
            .orderBy("leaveApplyDate", Query.Direction.DESCENDING)
            .whereGreaterThanOrEqualTo("endDate", getTodayStartTimeStamp())
    }

    override suspend fun getLeaveRequestBy(
        staffIds: List<String>,
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): List<DocumentSnapshot> {
        return suspendCancellableCoroutine { continuation ->
            leaveRequestCollection
                .orderBy("endDate")
                .whereLessThanOrEqualTo("endDate", endDate.toTimeStamp())
                .whereGreaterThanOrEqualTo("endDate", startDate.toTimeStamp())
                .whereIn("staffId", staffIds)
                .get()
                .addOnSuccessListener {
                    continuation.resume(it.documents)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun addLeaveRequest(model: LeaveRequestModel): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val document = leaveRequestCollection.document()
            document.set(model.copy(id = document.id).toFireStoreMap())
                .addContinuationBooleanListener(continuation)
        }
    }

    override suspend fun deleteLeaveRequest(id: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            leaveRequestCollection.document(id)
                .delete()
                .addContinuationBooleanListener(continuation)
        }
    }

    override suspend fun updateLeaveRequestStatus(
        id: String,
        approverId: String,
        status: LeaveStatus
    ): Boolean {
        return suspendCancellableCoroutine { continuation ->
            leaveRequestCollection.document(id)
                .update(
                    mapOf(
                        "leaveStatus" to status.name,
                        "leaveApprovedDate" to if (status == LeaveStatus.Approved) OffsetDateTime.now().toTimeStamp() else null,
                        "leaveRejectedDate" to if (status == LeaveStatus.Rejected) OffsetDateTime.now().toTimeStamp() else null,
                        "approverId" to approverId
                    )
                )
                .addContinuationBooleanListener(continuation)
        }
    }

    override suspend fun addEvent(model: EventModel): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val document = eventCollection.document()
            document.set(model.copy(id = document.id).toFireStoreMap())
                .addContinuationBooleanListener(continuation)
        }
    }

    override suspend fun getAllEvents(): List<DocumentSnapshot> {
        return suspendCancellableCoroutine { continuation ->
            eventCollection
                .orderBy("date")
                .get()
                .addOnSuccessListener { continuation.resume(it.documents.toList()) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    override suspend fun updateEvent(model: EventModel): Boolean {
        return suspendCancellableCoroutine { continuation ->
            eventCollection.document(model.id)
                .update(model.toFireStoreMap())
                .addContinuationBooleanListener(continuation)
        }
    }

    override suspend fun deleteEvent(id: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            eventCollection.document(id)
                .delete()
                .addContinuationBooleanListener(continuation)
        }
    }

    override suspend fun getAllUpcomingEvents(): List<DocumentSnapshot> {
        return suspendCancellableCoroutine { continuation ->
            eventCollection
                .orderBy("date")
                .startAt(getTodayStartTimeStamp())
                .get()
                .addOnSuccessListener { continuation.resume(it.documents.toList()) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    override fun getRTAllUpcomingEvents(): Query {
        return eventCollection
            .orderBy("date")
            .startAt(getTodayStartTimeStamp())
    }
}

private fun Task<DocumentSnapshot>.addContinuationListener(continuation: CancellableContinuation<DocumentSnapshot>) {
    this
        .addOnSuccessListener {
            continuation.resume(it)
        }
        .addOnFailureListener {
            continuation.resumeWithException(it)
        }
}

private fun Task<Void>.addContinuationBooleanListener(continuation: CancellableContinuation<Boolean>) {
    this
        .addOnSuccessListener {
            continuation.resume(true)
        }
        .addOnFailureListener {
            continuation.resume(false)
        }
}

