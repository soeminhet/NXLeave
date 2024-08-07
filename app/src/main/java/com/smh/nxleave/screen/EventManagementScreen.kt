package com.smh.nxleave.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.nxleave.design.component.EventManagementItem
import com.smh.nxleave.design.component.NXAlertDialog
import com.smh.nxleave.design.component.NXBackButton
import com.smh.nxleave.design.component.NXFloatingButton
import com.smh.nxleave.design.component.NXLoading
import com.smh.nxleave.design.sheet.EventManagementSheet
import com.smh.nxleave.design.sheet.Option
import com.smh.nxleave.design.sheet.OptionsContent
import com.smh.nxleave.design.sheet.SheetPreview
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.viewmodel.EventManagementUiEvent
import com.smh.nxleave.viewmodel.EventManagementUiState
import com.smh.nxleave.viewmodel.EventManagementViewModel
import com.smh.nxleave.viewmodel.RoleUiEvent
import kotlinx.coroutines.flow.collectLatest
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Composable
fun EventManagementScreen(
    onBack: () -> Unit,
    viewModel: EventManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.loading) NXLoading()

    var showExistDialog by remember { mutableStateOf(false) }
    if (showExistDialog) {
        NXAlertDialog(
            title = "Sorry",
            body = "Event name already exist.",
            confirmButton = { showExistDialog = false }
        )
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvent.collectLatest {
            when(it) {
                EventManagementUiEvent.AddEventError -> {}
                EventManagementUiEvent.DeleteEventError -> {}
                EventManagementUiEvent.EventExist -> showExistDialog = true
                EventManagementUiEvent.UpdateEventError -> {}
            }
        }
    }

    EventManagementContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                EventManagementUserEvent.OnBack -> onBack()
                is EventManagementUserEvent.AddNewEvent -> viewModel.addEvent(it.model)
                is EventManagementUserEvent.DeleteEvent -> viewModel.deleteEvent(it.model)
                is EventManagementUserEvent.UpdateEvent -> viewModel.updateEvent(it.model)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventManagementContent(
    uiState: EventManagementUiState,
    userEvent: (EventManagementUserEvent) -> Unit
) {
    var addEventSheet by remember { mutableStateOf(false) }
    var editEventData by remember { mutableStateOf<EventModel?>(null) }
    var deleteEventData by remember { mutableStateOf<EventModel?>(null) }

    if (addEventSheet) {
        EventManagementSheet(
            onDismissRequest = { addEventSheet = false },
            onSubmit = {
                userEvent(EventManagementUserEvent.AddNewEvent(it))
                addEventSheet = false
            }
        )
    }

    editEventData?.let { model ->
        EventManagementSheet(
            model = model,
            onDismissRequest = { editEventData = null },
            onSubmit = {
                userEvent(EventManagementUserEvent.UpdateEvent(it))
                editEventData = null
            }
        )
    }

    deleteEventData?.let {
        NXAlertDialog(
            title = "Delete",
            body = "Are you sure wan to delete ${it.name}?",
            confirmButton = {
                userEvent(EventManagementUserEvent.DeleteEvent(it))
                deleteEventData = null
            },
            dismissButton = { deleteEventData = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Events") },
                navigationIcon = {
                    NXBackButton(onBack = { userEvent(EventManagementUserEvent.OnBack) })
                },
            )
        },
        floatingActionButton = {
            NXFloatingButton(
                onClick = { addEventSheet = true }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(bottom = spacing.space12,),
        ) {
            items(
                uiState.events,
                key = { event -> event.id }
            ) { event ->
                EventManagementItem(
                    model = event,
                    enabled = true,
                    onEdit = { model -> editEventData = model },
                    onDelete = { model -> deleteEventData = model }
                )
            }
        }
    }
}

sealed interface EventManagementUserEvent {
    data object OnBack: EventManagementUserEvent
    data class AddNewEvent(val model: EventModel): EventManagementUserEvent
    data class UpdateEvent(val model: EventModel): EventManagementUserEvent
    data class DeleteEvent(val model: EventModel): EventManagementUserEvent
}

@Preview
@Composable
private fun EventManagementPreview() {
    NXLeaveTheme {
        SheetPreview(
            content = {
                EventManagementContent(
                    uiState = EventManagementUiState()
                        .copy(
                            events = listOf(
                                EventModel("1", "Union Day", OffsetDateTime.of(2024, 2, 12, 0, 0, 0, 0, ZoneOffset.UTC)),
                                EventModel("2", "Thingyan", OffsetDateTime.of(2024, 4, 14, 0, 0, 0, 0, ZoneOffset.UTC)),
                            )
                        ),
                    userEvent = {}
                )
            },
            sheet = {
                OptionsContent(
                    options = listOf(Option.EDIT, Option.DELETE),
                    onClick = {},
                    onCancel = {}
                )
            }
        )
    }
}