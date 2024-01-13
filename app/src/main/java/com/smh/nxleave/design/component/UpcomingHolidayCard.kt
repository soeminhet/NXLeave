package com.smh.nxleave.design.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.utility.DATE_PATTERN_TWO

@Composable
fun UpcomingHolidayCard(
    event: EventModel
) {
    Card() {
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .height(100.dp)
                .width(150.dp)
        ) {
            Text(
                text = event.date.dayOfWeek.name,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = event.date.format(DATE_PATTERN_TWO),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = event.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpcomingHolidayCardPreview() {
    NXLeaveTheme {
        Box(modifier = Modifier.padding(spacing.space20)) {
            UpcomingHolidayCard(
                event = EventModel.example
            )
        }
    }
}