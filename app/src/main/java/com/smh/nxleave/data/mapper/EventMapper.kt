package com.smh.nxleave.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.utility.toOffsetDateTime
import com.smh.nxleave.utility.toTimeStamp
import java.time.OffsetDateTime

fun EventModel.toFireStoreMap(): HashMap<String, Any?> = hashMapOf(
    "id" to id,
    "name" to name,
    "date" to date.toTimeStamp()
)

fun DocumentSnapshot.toEventModel(): EventModel {
    return EventModel(
        id = getString("id").orEmpty(),
        name = getString("name").orEmpty(),
        date = getTimestamp("date")?.toOffsetDateTime() ?: OffsetDateTime.now()
    )
}