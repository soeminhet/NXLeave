package com.smh.nxleave.screen.bottomnav

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.nxleave.LocalEntryPadding
import com.smh.nxleave.design.component.Label
import com.smh.nxleave.design.component.NXLoading
import com.smh.nxleave.design.component.NXProfile
import com.smh.nxleave.design.component.ShowLeaveItem
import com.smh.nxleave.design.component.UpcomingHolidayCard
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.viewmodel.DashboardUiState
import com.smh.nxleave.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    toAllUpcomingEvents: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DashboardContent(
        uiState = uiState,
        toAllUpcomingEvents = toAllUpcomingEvents
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    toAllUpcomingEvents: () -> Unit,
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(
                        horizontal = spacing.space20,
                        vertical = spacing.space12
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello ${uiState.currentStaff?.name ?: ""}",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = uiState.todayDate,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                NXProfile(
                    url = uiState.currentStaff?.photo.orEmpty(),
                    size = 50.dp,
                )
            }
        },
        modifier = Modifier.padding(LocalEntryPadding.current),
        contentWindowInsets = WindowInsets(top = 0, bottom = 0),
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .animateContentSize(),
        ) {
            stickyHeader {
                Label(
                    value = "Upcoming Holidays",
                    modifier = Modifier
                        .animateItemPlacement()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(
                            start = spacing.horizontalSpace,
                            end = spacing.space10,
                            top = spacing.space10
                        ),
                    onViewAll = toAllUpcomingEvents
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.animateItemPlacement()
                ) {
                    items(
                        uiState.upcomingEvents,
                        key = { event -> event.id }
                    ) { model ->
                        UpcomingHolidayCard(
                            event = model
                        )
                    }
                }
            }

            stickyHeader {
                Label(
                    value = "Upcoming Leaves",
                    modifier = Modifier
                        .animateItemPlacement()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(
                            start = spacing.horizontalSpace,
                            end = spacing.space10,
                            top = spacing.space10
                        )
                )
            }

            items(uiState.leaveRequests) { request ->
                ShowLeaveItem(
                    modifier = Modifier
                        .animateItemPlacement()
                        .padding(horizontal = spacing.horizontalSpace)
                        .padding(bottom = spacing.space16),
                    status = request.leaveStatus,
                    title = request.staffName,
                    dates = request.dateRange,
                    role = request.role,
                    leaveType = request.leaveType
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    NXLeaveTheme {
        DashboardContent(
            uiState = DashboardUiState(),
            toAllUpcomingEvents = {}
        )
    }
}