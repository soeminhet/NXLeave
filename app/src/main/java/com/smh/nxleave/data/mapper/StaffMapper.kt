package com.smh.nxleave.data.mapper

import com.smh.nxleave.domain.model.StaffModel

fun StaffModel.toFireStoreMap(): Map<String, Any> = hashMapOf(
    "id" to id,
    "roleId" to roleId,
    "name" to name,
    "email" to email,
    "phoneNumber" to phoneNumber,
    "currentProjectIds" to currentProjectIds,
    "photo" to photo,
    "enable" to enable,
)

@Suppress("UNCHECKED_CAST")
fun Map<String, Any>.toStaffModel(): StaffModel = StaffModel(
    id = get("id") as String,
    roleId = get("roleId") as String,
    name = get("name") as String,
    email = get("email") as String,
    phoneNumber = get("phoneNumber") as String,
    currentProjectIds = get("currentProjectIds") as List<String>,
    photo = get("photo") as String,
    enable = get("enable") as? Boolean ?: false,
)