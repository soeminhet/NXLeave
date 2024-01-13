package com.smh.nxleave.data.mapper

import com.smh.nxleave.domain.model.ProjectModel

fun ProjectModel.toFireStoreMap(): Map<String, Any> = hashMapOf(
    "id" to id,
    "name" to name,
    "enable" to enable
)

fun Map<String, Any>.toProjectModel(): ProjectModel = ProjectModel(
    id = get("id") as String,
    name = get("name") as String,
    managerName = "",
    enable = get("enable") as? Boolean ?: false
)