package com.smh.nxleave.screen.model

data class StaffProfileUiModel(
    val id: String,
    val photo: String,
    val name: String,
    val role: String,
    val email: String,
    val phoneNumber: String,
    val roleId: String,
    val currentProjectIds: List<String>,
    val enable: Boolean,
)