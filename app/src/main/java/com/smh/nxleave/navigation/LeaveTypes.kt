package com.smh.nxleave.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.smh.nxleave.screen.LeaveTypesScreen

@Destination
@Composable
fun LeaveTypes(
    navigator: DestinationsNavigator
) {
    LeaveTypesScreen(
        onBack = navigator::popBackStack
    )
}