package com.smh.nxleave.domain.mapper

import com.smh.nxleave.domain.model.LeaveBalanceModel
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.screen.model.LeaveBalanceUiModel

fun LeaveBalanceModel.toUiModel(
    leaveTypeModel: LeaveTypeModel,
): LeaveBalanceUiModel {
    return LeaveBalanceUiModel(
        id = id,
        roleId = roleId,
        leaveTypeId = leaveTypeId,
        leaveTypeName = leaveTypeModel.name,
        balance = balance.toString()
    )
}

fun LeaveBalanceUiModel.toModel(): LeaveBalanceModel {
    return LeaveBalanceModel(
        id = id,
        roleId = roleId,
        leaveTypeId = leaveTypeId,
        balance = balance.toIntOrNull() ?: 0
    )
}