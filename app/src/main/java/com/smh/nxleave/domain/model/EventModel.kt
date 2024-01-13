package com.smh.nxleave.domain.model

import java.time.OffsetDateTime

data class EventModel(
    override val id: String,
    val name: String,
    val date: OffsetDateTime
): Identifiable {
    companion object {
        val example = EventModel(id = "1", name = "Thingyan", OffsetDateTime.now())
    }
}