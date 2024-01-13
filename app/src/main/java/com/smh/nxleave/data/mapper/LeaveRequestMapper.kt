package com.smh.nxleave.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.smh.nxleave.screen.model.LeaveRequestModel
import com.smh.nxleave.utility.toOffsetDateTime
import com.smh.nxleave.utility.toTimeStamp
import java.time.OffsetDateTime

fun LeaveRequestModel.toFireStoreMap(): HashMap<String, Any?> = hashMapOf(
    "id" to id,
    "staffId" to staffId,
    "leaveTypeId" to leaveTypeId,
    "approverId" to approverId,
    "duration" to duration.toString(),
    "startDate" to  startDate.toTimeStamp(),
    "endDate" to endDate?.toTimeStamp(),
    "period" to period,
    "description" to description,
    "leaveStatus" to leaveStatus,
    "leaveApplyDate" to leaveApplyDate.toTimeStamp(),
    "leaveApprovedDate" to leaveApprovedDate?.toTimeStamp(),
    "leaveRejectedDate" to leaveRejectedDate?.toTimeStamp(),
)

fun DocumentSnapshot.toLeaveRequestModel(): LeaveRequestModel {
    return LeaveRequestModel(
        id = getString("id").orEmpty(),
        staffId = getString("staffId").orEmpty(),
        leaveTypeId = getString("leaveTypeId").orEmpty(),
        approverId = getString("approverId").orEmpty(),
        duration = (getString("duration"))?.toDoubleOrNull() ?: 0.0,
        startDate =  getTimestamp("startDate")?.toOffsetDateTime() ?: OffsetDateTime.now(),
        endDate = getTimestamp("endDate")?.toOffsetDateTime(),
        description = getString("description").orEmpty(),
        leaveStatus = getString("leaveStatus").orEmpty(),
        leaveApplyDate = getTimestamp("leaveApplyDate")?.toOffsetDateTime() ?: OffsetDateTime.now(),
        leaveApprovedDate = getTimestamp("leaveApprovedDate")?.toOffsetDateTime(),
        leaveRejectedDate = getTimestamp("leaveRejectedDate")?.toOffsetDateTime(),
        period = getString("period"),
    )
}