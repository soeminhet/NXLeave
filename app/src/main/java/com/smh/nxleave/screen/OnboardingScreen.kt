package com.smh.nxleave.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.smh.nxleave.design.component.NXLoading
import com.smh.nxleave.ui.theme.DMSerifDisplayRegular
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.viewmodel.OnboardingUiEvent
import com.smh.nxleave.viewmodel.OnboardingUiState
import com.smh.nxleave.viewmodel.OnboardingViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OnboardingScreen(
    toLogin: () -> Unit,
    toDashboard: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.loading) NXLoading()

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvent.collectLatest {
            when(it) {
                is OnboardingUiEvent.GetStartFail -> {}
                OnboardingUiEvent.GetStartSuccess -> toDashboard()
            }
        }
    }

    OnboardingContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                OnboardingUserEvent.ToLogin -> toLogin()
                OnboardingUserEvent.GetStarted -> viewModel.getStarted()
            }
        }
    )
}

@Composable
private fun OnboardingContent(
    uiState: OnboardingUiState,
    userEvent: (OnboardingUserEvent) -> Unit
) {
    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = spacing.horizontalSpace),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "NXLeave",
                fontFamily = DMSerifDisplayRegular,
                style = MaterialTheme.typography.displayMedium
            )

            Text(
                text = "Take flight from paperwork.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.space10)
            ) {
                Button(
                    onClick = {
                        if (uiState.isInitialized) {
                            userEvent(OnboardingUserEvent.ToLogin)
                        } else {
                            userEvent(OnboardingUserEvent.GetStarted)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = if (uiState.isInitialized) "LOGIN" else "GET STARTED")
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

sealed interface OnboardingUserEvent {
    data object ToLogin: OnboardingUserEvent
    data object GetStarted: OnboardingUserEvent
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPreview() {
    NXLeaveTheme {
        OnboardingContent(
            uiState = OnboardingUiState(),
            userEvent = {}
        )
    }
}