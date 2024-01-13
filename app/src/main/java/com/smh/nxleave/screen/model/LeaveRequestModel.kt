package com.smh.nxleave.screen.model

import java.time.OffsetDateTime

data class LeaveRequestModel(
    val id: String,
    val staffId: String,
    val leaveTypeId: String,
    val approverId: String,
    val duration: Double,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime?,
    val period: String?,
    val description: String,
    val leaveStatus: String,
    val leaveApplyDate: OffsetDateTime,
    val leaveApprovedDate: OffsetDateTime?,
    val leaveRejectedDate: OffsetDateTime?,
)