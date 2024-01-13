package com.smh.nxleave.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.smh.nxleave.screen.RolesScreen

@Destination
@Composable
fun Roles(
    navigator: DestinationsNavigator
) {
    RolesScreen(
        onBack = navigator::popBackStack
    )
}