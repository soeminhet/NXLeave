package com.smh.nxleave.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.smh.nxleave.screen.ProjectsScreen

@Destination
@Composable
fun Projects(
    navigator: DestinationsNavigator
) {
    ProjectsScreen(
        onBack = navigator::popBackStack
    )
}