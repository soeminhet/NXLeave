package com.smh.nxleave.design.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.design.component.NXOutlinedTextField
import com.smh.nxleave.design.component.SheetLip
import com.smh.nxleave.design.component.modifier.noRippleClick
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventManagementSheet(
    model: EventModel? = null,
    onDismissRequest: () -> Unit,
    onSubmit: (EventModel) -> Unit
) {
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0.dp),
        dragHandle = null,
        tonalElevation = 0.dp,
    ) {
        EventManagementContent(
            model = model,
            onSubmit = onSubmit
        )
    }
}

@Composable
private fun EventManagementContent(
    model: EventModel?,
    onSubmit: (EventModel) -> Unit,
) {
    var name by remember { mutableStateOf(model?.name.orEmpty()) }
    var date by remember { mutableStateOf<OffsetDateTime?>(model?.date) }
    val submitEnable by remember {
        derivedStateOf {
            name.isNotBlank() && date != null
        }
    }

    var showDatePickerSheet by remember { mutableStateOf(false) }
    if (showDatePickerSheet) {
        NXDatePickerSheet(
            selectedDate = date?.toInstant()?.toEpochMilli(),
            onSelectedDate = {
                date = it
                showDatePickerSheet = false
            },
            dateValidator = { timestamp ->
                timestamp > Instant.now().minusSeconds(86400).toEpochMilli()
            },
            onDismissRequest = { showDatePickerSheet = false },
            title = "Select Date"
        )
    }

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .padding(bottom = 40.dp)
            .padding(horizontal = spacing.horizontalSpace)
    ) {
        SheetLip()

        Text(
            text = if (model == null) "Add Event" else "Edit Event",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        NXOutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.padding(top = spacing.space16),
            label = "Name"
        )

        NXOutlinedTextField(
            label = "Date",
            value = date?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "",
            onValueChange = {},
            topLabel = true,
            readOnly = true,
            enabled = false,
            modifier = Modifier
                .padding(top = spacing.space16)
                .noRippleClick { showDatePickerSheet = true },
            colors = OutlinedTextFieldDefaults.colors(
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledTextColor = MaterialTheme.colorScheme.onSurface
            ),
        )

        Button(
            onClick = {
                val newModel = model?.copy(
                    name = name,
                    date = date!!
                ) ?: EventModel(id = "", name = name, date = date!!)
                onSubmit(newModel)
            },
            modifier = Modifier
                .padding(top = spacing.space20)
                .padding(bottom = 30.dp)
                .fillMaxWidth(),
            enabled = submitEnable
        ) {
            Text(text = "SUBMIT")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EventManagementPreview() {
    NXLeaveTheme {
        EventManagementContent(
            model = null,
            onSubmit = {}
        )
    }
}