package com.smh.nxleave.domain.model

import java.util.UUID

data class ProjectModel(
    override val id: String,
    val name: String,
    val enable: Boolean,
): Identifiable {
    companion object {
        val example = ProjectModel(
            id = UUID.randomUUID().toString(),
            name = "Trifecta",
            enable = true
        )

        val example2 = ProjectModel(
            id = UUID.randomUUID().toString(),
            name = "AIA",
            enable = true
        )

        val allProject = ProjectModel(
            id = "",
            name = "All",
            enable = true
        )
    }
}