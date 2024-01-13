package com.smh.nxleave.screen.model

import NX_Red
import androidx.compose.ui.graphics.Color
import java.util.UUID

data class MyLeaveBalanceUiModel(
    val id: String,
    val color: Color,
    val name: String,
    val took: Double,
    val total: Double,
    val enable: Boolean,
) {
    companion object {
        val exampleEnable = MyLeaveBalanceUiModel(
            id = UUID.randomUUID().toString(),
            color = NX_Red,
            name = "Name",
            took = 5.0,
            total = 10.0,
            enable = true
        )

        val exampleDisable = MyLeaveBalanceUiModel(
            id = UUID.randomUUID().toString(),
            color = NX_Red,
            name = "Name",
            took = 5.0,
            total = 10.0,
            enable = false
        )
    }
}