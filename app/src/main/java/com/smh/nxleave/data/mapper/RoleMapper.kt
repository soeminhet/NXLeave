package com.smh.nxleave.data.mapper

import com.smh.nxleave.domain.model.AccessLevel.Companion.toAccessLevel
import com.smh.nxleave.domain.model.RoleModel

fun RoleModel.toFireStoreMap(): Map<String, Any> = hashMapOf(
    "id" to id,
    "name" to name,
    "enable" to enable,
    "accessLevel" to accessLevel.id
)

fun Map<String, Any>.toRoleModel(): RoleModel = RoleModel(
    id = get("id") as String,
    name = get("name") as String,
    enable = get("enable") as? Boolean ?: false,
    accessLevel = (get("accessLevel") as? String).toAccessLevel()
)