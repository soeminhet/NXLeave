package com.smh.nxleave.data.mapper

import com.smh.nxleave.domain.model.LeaveBalanceModel

fun LeaveBalanceModel.toFireStoreMap(): Map<String, Any> = hashMapOf(
    "id" to id,
    "roleId" to roleId,
    "leaveTypeId" to leaveTypeId,
    "balance" to balance
)

fun Map<String, Any>.toLeaveRequestModel(): LeaveBalanceModel = LeaveBalanceModel(
    id = get("id") as String,
    roleId = get("roleId") as String,
    leaveTypeId = get("leaveTypeId") as String,
    balance = (get("balance") as Long).toInt(),
)