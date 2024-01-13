package com.smh.nxleave.domain.model

import java.util.UUID

data class StaffModel(
    override val id: String,
    val roleId: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val currentProjectIds: List<String>,
    val photo: String,
    val enable: Boolean
): Identifiable {
    companion object {
        val projectManager = StaffModel(
            id = UUID.randomUUID().toString(),
            roleId = RoleModel.projectManager.id,
            name = "Dexter",
            email = "dexter@nxleave.co",
            phoneNumber = "09111222333",
            currentProjectIds = listOf("Trifecta"),
            photo = "",
            enable = true,
        )

        val allStaff = StaffModel(
            id = "",
            roleId = "",
            name = "All",
            email = "",
            phoneNumber = "",
            currentProjectIds = emptyList(),
            photo = "",
            enable = true,
        )
    }
}