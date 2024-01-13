package com.smh.nxleave.design.sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NXDatePickerSheet(
    selectedDate: Long? = Instant.now().toEpochMilli(),
    title: String = "Select Date",
    dateValidator: (Long) -> Boolean = { true },
    onSelectedDate: (OffsetDateTime) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0),
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        NXDatePickerSheetContent(
            title = title,
            selectedDate = selectedDate,
            dateValidator = dateValidator,
            onSelectedDate = onSelectedDate,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NXDatePickerSheetContent(
    title: String,
    onSelectedDate: (OffsetDateTime) -> Unit,
    dateValidator: (Long) -> Boolean,
    selectedDate: Long?
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    LaunchedEffect(key1 = Unit) {
        snapshotFlow { datePickerState.selectedDateMillis }
            .drop(1)
            .filterNotNull()
            .distinctUntilChanged()
            .collectLatest {
                onSelectedDate(Instant.ofEpochMilli(it).atOffset(ZoneOffset.UTC))
            }
    }

    DatePicker(
        state = datePickerState,
        dateValidator = dateValidator,
        title = {
            Text(
                text = title,
                modifier = Modifier
                    .padding(PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp))
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
fun NXDatePickerSheetPreview() {
    NXLeaveTheme {
        Box(modifier = Modifier.padding(spacing.space10)) {
            NXDatePickerSheetContent(
                onSelectedDate = {},
                title = "Select Date",
                dateValidator = { true },
                selectedDate = Instant.now().toEpochMilli()
            )
        }
    }
}