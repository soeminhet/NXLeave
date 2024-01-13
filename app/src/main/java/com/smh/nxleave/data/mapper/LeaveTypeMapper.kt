package com.smh.nxleave.data.mapper

import com.smh.nxleave.domain.model.LeaveTypeModel

fun LeaveTypeModel.toFireStoreMap(): Map<String, Any> = hashMapOf(
    "id" to id,
    "name" to name,
    "color" to color,
    "enable" to enable
)

fun Map<String, Any>.toLeaveTypeModel(): LeaveTypeModel = LeaveTypeModel(
    id = get("id") as String,
    name = get("name") as String,
    color = get("color") as? Long ?: 0xFFFF004D,
    enable = get("enable") as? Boolean ?: false
)