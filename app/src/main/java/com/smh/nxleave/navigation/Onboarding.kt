package com.smh.nxleave.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.smh.nxleave.navigation.destinations.DashboardDestination
import com.smh.nxleave.navigation.destinations.LoginDestination
import com.smh.nxleave.navigation.destinations.ResetPasswordDestination
import com.smh.nxleave.screen.OnboardingScreen

@RootNavGraph(start = true)
@Destination
@Composable
fun Onboarding(
    navigator: DestinationsNavigator
) {
    OnboardingScreen(
        toLogin = { navigator.navigate(LoginDestination) },
        toDashboard = { navigator.navigate(DashboardDestination) },
        toReset = { navigator.navigate(ResetPasswordDestination) }
    )
}