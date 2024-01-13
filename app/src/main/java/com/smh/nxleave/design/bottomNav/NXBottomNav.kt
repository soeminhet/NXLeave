package com.smh.nxleave.design.bottomNav

import NX_Charcoal_80
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.ramcosta.composedestinations.utils.currentDestinationFlow
import com.smh.nxleave.design.component.modifier.noRippleClick
import com.smh.nxleave.navigation.destinations.BalanceDestination
import com.smh.nxleave.navigation.destinations.DashboardDestination
import com.smh.nxleave.navigation.destinations.LeaveApproveDestination
import com.smh.nxleave.navigation.destinations.ProfileDestination
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull

enum class NXNavItem(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val route: String,
) {
    Dashboard(
        icon = Icons.Outlined.Dashboard,
        selectedIcon = Icons.Default.Dashboard,
        route = DashboardDestination.route,
    ),
    Balance(
        icon = Icons.Outlined.Balance,
        selectedIcon = Icons.Default.Balance,
        route = BalanceDestination.route
    ),
    Approve(
        icon = Icons.Outlined.Task,
        selectedIcon = Icons.Filled.Task,
        route = LeaveApproveDestination.route
    ),
    Profile(
        icon = Icons.Outlined.Person,
        selectedIcon = Icons.Default.Person,
        route = ProfileDestination.route
    )
}

@Composable
fun NXBottomNav(
    navController: NavController,
    showApprove: Boolean
) {
    val staffRoutes = remember(showApprove) {
        if (showApprove) listOf(NXNavItem.Dashboard, NXNavItem.Balance, NXNavItem.Approve, NXNavItem.Profile)
        else listOf(NXNavItem.Dashboard, NXNavItem.Balance, NXNavItem.Profile)
    }
    var currentRoute by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        navController.currentDestinationFlow
            .mapNotNull { it.route }
            .distinctUntilChanged()
            .collectLatest {
                currentRoute = it
            }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Divider()
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 0.dp,
            modifier = Modifier.padding(horizontal = 2.dp)
        ) {
            staffRoutes.forEach { item ->
                NXBottomBarItem(
                    icon = item.icon,
                    selectedIcon = item.selectedIcon,
                    value = item.name,
                    isSelected =  currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RowScope.NXBottomBarItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    value: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else NX_Charcoal_80,
        label = "ContentColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .noRippleClick(onClick = onClick)
            .weight(1f)
            .padding(
                top = 16.dp,
                bottom = 8.dp
            )
    ) {
        Icon(
            imageVector = if(isSelected) selectedIcon else icon,
            contentDescription = value,
            tint = contentColor
        )

        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}