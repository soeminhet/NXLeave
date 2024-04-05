package com.smh.nxleave.domain.model

import java.util.UUID

sealed interface AccessLevel: Identifiable {
    val name: String
    data class All(override val id: String = "1", override val name: String = "All"): AccessLevel
    data class Approve(override val id: String = "2", override val name: String = "Approve"): AccessLevel
    data class None(override val id: String = "3", override val name: String = "None"): AccessLevel

    companion object {
        fun String?.toAccessLevel(): AccessLevel {
            return when {
                this == "1" -> All()
                this == "2" -> Approve()
                else -> None()
            }
        }

        val list = listOf(
            All(),
            Approve(),
            None()
        )
    }
}

data class RoleModel(
    override val id: String,
    val name: String,
    val enable: Boolean,
    val accessLevel: AccessLevel
): Identifiable {
    companion object {
        val projectManager = RoleModel(
            id = UUID.randomUUID().toString(),
            name = "Project Manager",
            enable = true,
            accessLevel = AccessLevel.Approve()
        )

        val androidDeveloper = RoleModel(
            id = UUID.randomUUID().toString(),
            name = "Android Developer",
            enable = true,
            accessLevel = AccessLevel.None()
        )

        val iOSDeveloper = RoleModel(
            id = UUID.randomUUID().toString(),
            name = "iOS Developer",
            enable = true,
            accessLevel = AccessLevel.None()
        )

        val allRole = RoleModel(
            id = "",
            name = "All",
            enable = true,
            accessLevel = AccessLevel.None()
        )
    }
}