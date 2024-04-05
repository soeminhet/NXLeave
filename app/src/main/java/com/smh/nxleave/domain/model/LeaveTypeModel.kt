package com.smh.nxleave.domain.model

import java.util.UUID

data class LeaveTypeModel(
    override val id: String,
    val name: String,
    val color: Long,
    var enable: Boolean,
): Identifiable {
    companion object {
        val annualLeave = LeaveTypeModel(
            id = UUID.randomUUID().toString(),
            name = "Annual Leave",
            color = 0xFFFF004D,
            enable = true
        )

        val medicalLeave = LeaveTypeModel(
            id = UUID.randomUUID().toString(),
            name = "Medical Leave",
            color = 0xFF80BCBD,
            enable = true
        )
    }
}