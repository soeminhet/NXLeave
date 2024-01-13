package com.smh.nxleave.screen.model

import com.smh.nxleave.domain.model.Identifiable

data class PeriodModel(
    override val id: String,
    val name: String
): Identifiable {
    companion object {
        val periods = listOf(
            PeriodModel(
                id = "AM",
                name = "AM"
            ),
            PeriodModel(
                id = "PM",
                name = "PM"
            )
        )
    }
}