package com.smh.nxleave.screen.bottomnav

import NX_Grey
import NX_White
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.LocalPolice
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.TypeSpecimen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.nxleave.LocalEntryPadding
import com.smh.nxleave.R
import com.smh.nxleave.design.component.Label
import com.smh.nxleave.design.component.NXAlertDialog
import com.smh.nxleave.design.component.NXProfile
import com.smh.nxleave.design.sheet.Option
import com.smh.nxleave.design.sheet.OptionsSheet
import com.smh.nxleave.design.sheet.PrivacyPolicySheet
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.utility.callPhone
import com.smh.nxleave.utility.goToSetting
import com.smh.nxleave.utility.sendEmail
import com.smh.nxleave.viewmodel.ProfileUiState
import com.smh.nxleave.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navigationEvent: (ProfileScreenNavigationEvent) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                ProfileUserEvent.OnLogout -> viewModel.logout()
            }
        },
        navigationEvent = navigationEvent
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    userEvent: (ProfileUserEvent) -> Unit,
    navigationEvent: (ProfileScreenNavigationEvent) -> Unit,
) {
    val context = LocalContext.current
    var showPolicySheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showContactUsOptionsSheet by remember { mutableStateOf(false) }
    var permissionRequested by remember { mutableStateOf<String?>(null) }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                context.callPhone()
            } else {
                permissionRequested = "Please enable call phone permission at setting."
            }
        }

    if (permissionRequested != null) {
        NXAlertDialog(
            title = "Permission required",
            body = permissionRequested!!,
            dismissButton = { permissionRequested = null },
            confirmButton = {
                permissionRequested = null
                context.goToSetting()
            }
        )
    }

    if (showPolicySheet) {
        PrivacyPolicySheet(
            onDismissRequest = { showPolicySheet = false }
        )
    }

    if (showLogoutDialog) {
        NXAlertDialog(
            title = "Logout",
            body = "Are you sure want to logout?",
            confirmButton = {
                userEvent(ProfileUserEvent.OnLogout)
                showLogoutDialog = false
            },
            dismissButton = { showLogoutDialog = false },
            confirmButtonText = "Logout",
            dismissButtonText = "Cancel"
        )
    }

    if (showContactUsOptionsSheet) {
        OptionsSheet(
            options = listOf(Option.EMAIL, Option.PHONE),
            onClick = {
                when(it) {
                    Option.EMAIL -> { context.sendEmail() }
                    Option.PHONE -> { permissionLauncher.launch(android.Manifest.permission.CALL_PHONE) }
                    else -> {}
                }
                showContactUsOptionsSheet = false
            },
            onDismissRequest = { showContactUsOptionsSheet = false }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.padding(LocalEntryPadding.current),
        contentWindowInsets = WindowInsets(top = 0, bottom = 0),
    ) {
        Column(
            modifier = Modifier.padding(it),
        ) {
            Box(
                modifier = Modifier
                    .height(intrinsicSize = IntrinsicSize.Min)
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 30.dp,
                            bottomEnd = 30.dp,
                        )
                    ),
                contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_profile),
                    contentDescription = "ProfileBackground",
                    modifier = Modifier
                        .height(260.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(0.1f),
                                    Color.Black.copy(0.3f),
                                    Color.Black.copy(0.5f),
                                ),
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 20.dp,
                            bottom = 16.dp
                        ),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    NXProfile(
                        url = uiState.photoURL,
                    )

                    Column(modifier = Modifier.padding(start = spacing.space4)) {
                        Text(
                            text = uiState.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = NX_White,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Text(
                            text = uiState.roleName,
                            style = MaterialTheme.typography.labelLarge,
                            color = NX_White.copy(alpha = 0.7f)
                        )
                    }
                }

                Button(
                    onClick = { navigationEvent(ProfileScreenNavigationEvent.ToEditProfile) },
                    colors = ButtonDefaults.buttonColors(containerColor = NX_White),
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(
                            top = 20.dp,
                            end = 20.dp
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = NX_Grey,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = "Edit",
                        color = NX_Grey,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.animateContentSize()
            ) {
                if (uiState.showManagement) {
                    stickyHeader {
                        Label(
                            value = "Management",
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .padding(
                                    start = 20.dp,
                                    top = 10.dp
                                )
                        )
                    }

                    item {
                        Column {
                            ProfileItem(
                                icon = Icons.Outlined.TypeSpecimen,
                                label = "Leave Types",
                                onClick = {
                                    navigationEvent(ProfileScreenNavigationEvent.ToLeaveTypes)
                                }
                            )

                            Divider()

                            ProfileItem(
                                icon = Icons.Outlined.Leaderboard,
                                label = "Roles",
                                onClick = {
                                    navigationEvent(ProfileScreenNavigationEvent.ToRoles)
                                }
                            )

                            Divider()

                            ProfileItem(
                                icon = Icons.Outlined.LocalPolice,
                                label = "Projects",
                                onClick = {
                                    navigationEvent(ProfileScreenNavigationEvent.ToProjects)
                                }
                            )

                            Divider()

                            ProfileItem(
                                icon = Icons.Outlined.AccountBalance,
                                label = "LeaveBalance",
                                onClick = {
                                    navigationEvent(ProfileScreenNavigationEvent.ToLeaveBalance)
                                }
                            )

                            Divider()

                            ProfileItem(
                                icon = Icons.Outlined.People,
                                label = "Staves",
                                onClick = {
                                    navigationEvent(ProfileScreenNavigationEvent.ToStaves)
                                }
                            )

                            Divider()

                            ProfileItem(
                                icon = Icons.Outlined.Event,
                                label = "Events",
                                onClick = {
                                    navigationEvent(ProfileScreenNavigationEvent.ToEvents)
                                }
                            )

                            Divider()

                            ProfileItem(
                                icon = Icons.Outlined.BarChart,
                                label = "Report",
                                onClick = {
                                    navigationEvent(ProfileScreenNavigationEvent.ToReport)
                                }
                            )
                        }
                    }
                }

                stickyHeader {
                    Label(
                        value = "Personal",
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(
                                start = 20.dp,
                                top = 10.dp
                            )
                    )
                }

                item {
                    Column {
                        ProfileItem(
                            icon = Icons.Default.People,
                            label = "Assigned team",
                            onClick = { navigationEvent(ProfileScreenNavigationEvent.ToAssignedTeam) }
                        )

                        Divider()

                        ProfileItem(
                            icon = Icons.Default.EventAvailable,
                            label = "Upcoming events",
                            onClick = { navigationEvent(ProfileScreenNavigationEvent.ToUpcomingEvents) }
                        )
                    }
                }

                stickyHeader {
                    Label(
                        value = "General",
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(
                                start = 20.dp,
                                top = 10.dp
                            )
                    )
                }

                item {
                    Column {
                        ProfileItem(
                            icon = Icons.Default.PrivacyTip,
                            label = "Privay Policy",
                            onClick = { showPolicySheet = true }
                        )

                        Divider()

                        ProfileItem(
                            icon = Icons.Default.Phone,
                            label = "Contact us",
                            onClick = { showContactUsOptionsSheet = true }
                        )

                        Divider()

                        ProfileItem(
                            icon = Icons.Default.Logout,
                            label = "Log out",
                            onClick = { showLogoutDialog = true }
                        )
                    }
                }

                item {
                    Text(
                        text = "NXLeave Version 1.0.0",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(all = 20.dp)
                    )
                }
            }
        }
    }
}

sealed interface ProfileUserEvent {
    data object OnLogout: ProfileUserEvent
}

sealed interface ProfileScreenNavigationEvent {
    data object ToLeaveTypes: ProfileScreenNavigationEvent
    data object ToRoles: ProfileScreenNavigationEvent
    data object ToProjects: ProfileScreenNavigationEvent
    data object ToLeaveBalance: ProfileScreenNavigationEvent
    data object ToStaves: ProfileScreenNavigationEvent
    data object ToEvents: ProfileScreenNavigationEvent
    data object ToEditProfile: ProfileScreenNavigationEvent
    data object ToAssignedTeam: ProfileScreenNavigationEvent
    data object ToUpcomingEvents: ProfileScreenNavigationEvent
    data object ToReport: ProfileScreenNavigationEvent
}

@Composable
private fun ProfileItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(Color.White)
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            Icon(
                imageVector = icon,
                contentDescription = ""
            )

            Text(
                text = label,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Detail"
            )
        }
    }
}

@Preview
@Composable
private fun ProfileContentPreview() {
    NXLeaveTheme {
        ProfileContent(
            uiState = ProfileUiState(
                name = "SoeMinHtet",
                roleName = "Android Developer",
                showManagement = true
            ),
            userEvent = {},
            navigationEvent = {}
        )
    }
}