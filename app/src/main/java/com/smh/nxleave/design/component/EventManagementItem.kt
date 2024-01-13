package com.smh.nxleave.design.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.smh.nxleave.design.sheet.Option
import com.smh.nxleave.design.sheet.OptionsSheet
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.utility.DATE_PATTERN_ONE

@Composable
fun EventManagementItem(
    model: EventModel,
    enabled: Boolean,
    onEdit: (EventModel) -> Unit,
    onDelete: (EventModel) -> Unit
) {
    var showOptionsSheet by remember { mutableStateOf(false) }

    if (showOptionsSheet) {
        OptionsSheet(
            options = listOf(
                Option.EDIT,
                Option.DELETE
            ),
            onClick = {
                when(it) {
                    Option.EDIT -> onEdit(model)
                    Option.DELETE -> onDelete(model)
                    else -> {}
                }
                showOptionsSheet = false
            },
            onDismissRequest = { showOptionsSheet = false }
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.space4),
        modifier = Modifier
            .clickable(enabled = enabled) { showOptionsSheet = true }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.horizontalSpace)
                .padding(vertical = spacing.space12)
        ) {
            Text(
                text = model.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = model.date.format(DATE_PATTERN_ONE),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Divider()
    }
}

@Preview(showBackground = true)
@Composable
fun EventManagementPreview() {
    NXLeaveTheme {
        Box(modifier = Modifier.padding(spacing.space10)) {
            EventManagementItem(
                model = EventModel.example,
                onEdit = {},
                onDelete = {},
                enabled = true
            )
        }
    }
}