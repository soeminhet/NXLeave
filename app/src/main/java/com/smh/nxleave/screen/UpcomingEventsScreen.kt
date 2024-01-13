package com.smh.nxleave.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.nxleave.design.component.EventManagementItem
import com.smh.nxleave.design.component.NXBackButton
import com.smh.nxleave.design.component.NXLoading
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.viewmodel.UpcomingEventsUiState
import com.smh.nxleave.viewmodel.UpcomingEventsViewModel

@Composable
fun UpcomingEventsScreen(
    onBack: () -> Unit,
    viewModel: UpcomingEventsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.loading) NXLoading()

    UpcomingEventsContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                UpcomingEventsUserEvent.OnBack -> onBack()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpcomingEventsContent(
    uiState: UpcomingEventsUiState,
    userEvent: (UpcomingEventsUserEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Upcoming Events") },
                navigationIcon = {
                    NXBackButton(onBack = { userEvent(UpcomingEventsUserEvent.OnBack) })
                },
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            items(
                uiState.events,
                key = { event -> event.id }
            ) {event ->
                EventManagementItem(
                    model = event,
                    enabled = false,
                    onEdit = {},
                    onDelete = {}
                )
            }
        }
    }
}

sealed interface UpcomingEventsUserEvent {
    data object OnBack: UpcomingEventsUserEvent
}

@Preview(showBackground = true)
@Composable
private fun UpcomingEventsPreview() {
    NXLeaveTheme {
        UpcomingEventsContent(
            userEvent = {},
            uiState = UpcomingEventsUiState(
                events = listOf(EventModel.example)
            )
        )
    }
}