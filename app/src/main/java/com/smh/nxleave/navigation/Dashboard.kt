package com.smh.nxleave.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.smh.nxleave.navigation.destinations.UpcomingEventsDestination
import com.smh.nxleave.screen.bottomnav.DashboardScreen

@Destination
@Composable
fun Dashboard(
    navigator: DestinationsNavigator
) {
    DashboardScreen(
        toAllUpcomingEvents = {
            navigator.navigate(UpcomingEventsDestination)
        }
    )
}