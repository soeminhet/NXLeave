package com.smh.nxleave.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
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
import com.smh.nxleave.design.component.NXBackButton
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.viewmodel.AssignedTeamViewModel

@Composable
fun AssignedTeamScreen(
    onBack: () -> Unit,
    viewModel: AssignedTeamViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AssignedTeamContent(
        teams = uiState.teams,
        userEvent = {
            when(it) {
                AssignedTeamUserEvent.OnBack -> onBack()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignedTeamContent(
    teams: List<String>,
    userEvent: (AssignedTeamUserEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "AssignedTeam") },
                navigationIcon = {
                    NXBackButton(onBack = { userEvent(AssignedTeamUserEvent.OnBack) })
                },
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
        ) {
            items(
                teams,
                key = { it }
            ) { team ->
                Column {
                    Text(
                        text = team,
                        modifier = Modifier
                            .defaultMinSize(minHeight = spacing.space48)
                            .padding(
                                vertical = spacing.space12,
                                horizontal = spacing.horizontalSpace
                            ),
                    )

                    Divider()
                }
            }
        }
    }
}

sealed interface AssignedTeamUserEvent {
    data object OnBack: AssignedTeamUserEvent
}

@Preview
@Composable
private fun AssignedTeamPreview() {
    NXLeaveTheme {
        AssignedTeamContent(
            teams = listOf("Team A", "Team B"),
            userEvent = {}
        )
    }
}