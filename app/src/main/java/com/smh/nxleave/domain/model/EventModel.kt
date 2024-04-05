package com.smh.nxleave.domain.model

import java.time.OffsetDateTime
import java.time.ZoneOffset

data class EventModel(
    override val id: String,
    val name: String,
    val date: OffsetDateTime
): Identifiable {
    companion object {
        val example = EventModel(
            id = "1",
            name = "Thingyan",
            date = OffsetDateTime.of(2024, 4, 14, 0, 0, 0, 0, ZoneOffset.UTC)
        )
    }
}