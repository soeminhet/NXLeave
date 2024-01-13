package com.smh.nxleave.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.smh.nxleave.navigation.destinations.AssignedTeamDestination
import com.smh.nxleave.navigation.destinations.EditProfileDestination
import com.smh.nxleave.navigation.destinations.EventManagementDestination
import com.smh.nxleave.navigation.destinations.LeaveBalanceDestination
import com.smh.nxleave.navigation.destinations.LeaveTypesDestination
import com.smh.nxleave.navigation.destinations.ProjectsDestination
import com.smh.nxleave.navigation.destinations.ReportDestination
import com.smh.nxleave.navigation.destinations.RolesDestination
import com.smh.nxleave.navigation.destinations.StaffManagementDestination
import com.smh.nxleave.navigation.destinations.UpcomingEventsDestination
import com.smh.nxleave.screen.bottomnav.ProfileScreen
import com.smh.nxleave.screen.bottomnav.ProfileScreenNavigationEvent

@Destination
@Composable
fun Profile(navigator: DestinationsNavigator) {
    ProfileScreen(
        navigationEvent = {
            when(it) {
                ProfileScreenNavigationEvent.ToLeaveTypes -> navigator.navigate(LeaveTypesDestination)
                ProfileScreenNavigationEvent.ToRoles -> navigator.navigate(RolesDestination)
                ProfileScreenNavigationEvent.ToProjects -> navigator.navigate(ProjectsDestination)
                ProfileScreenNavigationEvent.ToLeaveBalance -> navigator.navigate(LeaveBalanceDestination)
                ProfileScreenNavigationEvent.ToStaves -> navigator.navigate(StaffManagementDestination)
                ProfileScreenNavigationEvent.ToEvents -> navigator.navigate(EventManagementDestination)
                ProfileScreenNavigationEvent.ToEditProfile -> navigator.navigate(EditProfileDestination)
                ProfileScreenNavigationEvent.ToAssignedTeam -> navigator.navigate(AssignedTeamDestination)
                ProfileScreenNavigationEvent.ToUpcomingEvents -> navigator.navigate(UpcomingEventsDestination)
                ProfileScreenNavigationEvent.ToReport -> navigator.navigate(ReportDestination)
            }
        }
    )
}