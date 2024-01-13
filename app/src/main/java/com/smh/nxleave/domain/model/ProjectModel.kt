package com.smh.nxleave.domain.model

import java.util.UUID

data class ProjectModel(
    override val id: String,
    val name: String,
    val enable: Boolean,
    val managerName: String
): Identifiable {
    companion object {
        val example = ProjectModel(
            id = UUID.randomUUID().toString(),
            name = "Trifecta",
            managerName = "Dexter",
            enable = true
        )

        val allProject = ProjectModel(
            id = "",
            name = "All",
            managerName = "",
            enable = true
        )
    }
}