package com.smh.nxleave.domain.model

data class LeaveBalanceModel(
    override val id: String,
    val roleId: String,
    val leaveTypeId: String,
    val balance: Int
): Identifiable