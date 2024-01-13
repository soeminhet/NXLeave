package com.smh.nxleave.screen.model

import com.smh.nxleave.domain.model.Identifiable

data class LeaveBalanceUiModel(
    override val id: String,
    val roleId: String,
    val leaveTypeId: String,
    val leaveTypeName: String,
    val balance: String
): Identifiable
