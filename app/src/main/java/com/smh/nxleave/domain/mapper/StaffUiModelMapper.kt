package com.smh.nxleave.domain.mapper

import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.screen.model.StaffProfileUiModel

fun List<StaffModel>.toUIModels(roles: List<RoleModel>) = this.map {
    StaffProfileUiModel(
        id = it.id,
        photo = it.photo,
        name = it.name,
        role = roles.first { role -> role.id == it.roleId }.name,
        email = it.email,
        phoneNumber = it.phoneNumber,
        roleId = it.roleId,
        currentProjectIds = it.currentProjectIds,
        enable = it.enable,
    )
}

fun StaffProfileUiModel.toModel(): StaffModel {
    return StaffModel(
        id = id,
        roleId = roleId,
        name = name,
        phoneNumber = phoneNumber,
        email = email,
        currentProjectIds = currentProjectIds,
        photo = photo,
        enable = enable,
    )
}