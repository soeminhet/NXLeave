package com.smh.nxleave

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.utils.destination
import com.smh.nxleave.design.bottomNav.NXBottomNav
import com.smh.nxleave.design.component.animation.loadTransitions
import com.smh.nxleave.domain.model.AccessLevel
import com.smh.nxleave.navigation.NavGraphs
import com.smh.nxleave.navigation.destinations.BalanceDestination
import com.smh.nxleave.navigation.destinations.DashboardDestination
import com.smh.nxleave.navigation.destinations.LeaveApproveDestination
import com.smh.nxleave.navigation.destinations.OnboardingDestination
import com.smh.nxleave.navigation.destinations.ProfileDestination
import com.smh.nxleave.screen.content.AccountSuspendContent
import com.smh.nxleave.ui.theme.NXLeaveTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var notificationPermissionResultLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initPermissionLauncher()

        setContent {
            var keepSplash by remember { mutableStateOf(true) }
            splashScreen.setKeepOnScreenCondition { keepSplash }
            LaunchedEffect(key1 = Unit) {
                delay(1500)
                keepSplash = false
            }

            val isLogin by viewModel.isLogIn.collectAsStateWithLifecycle()
            val isAccountEnable by viewModel.isAccountEnable.collectAsStateWithLifecycle(true)
            val accessLevel by viewModel.accessLevel.collectAsStateWithLifecycle(AccessLevel.None())
            val startRoute = remember {
                derivedStateOf {
                    if (isLogin) DashboardDestination else OnboardingDestination
                }
            }
            var showBottomNav by remember { mutableStateOf(false) }
            var isMainEntry by remember { mutableStateOf(false) }

            val engine = rememberNavHostEngine(
                navHostContentAlignment = Alignment.Center,
                rootDefaultAnimations = RootNavGraphDefaultAnimations(
                    enterTransition = {
                        loadTransitions(isMainEntry = showBottomNav).enterTransition
                    },
                    exitTransition = {
                        loadTransitions(isMainEntry = showBottomNav).exitTransition
                    },
                    popEnterTransition = {
                        loadTransitions(isMainEntry = showBottomNav).popEnterTransition
                    },
                    popExitTransition = {
                        loadTransitions(isMainEntry = showBottomNav).popExitTransition
                    }
                )
            )
            val navController: NavHostController = engine.rememberNavController()

            LaunchedEffect(key1 = Unit) {
                navController.currentBackStackEntryFlow.collectLatest {
                    when(it.destination.route) {
                        DashboardDestination.route,
                        BalanceDestination.route,
                        LeaveApproveDestination.route,
                        ProfileDestination.route -> {
                            showBottomNav = true
                            isMainEntry = true
                        }
                        else -> {
                            showBottomNav = false
                            isMainEntry = false
                        }
                    }
                }
            }

            LaunchedEffect(key1 = startRoute.value) {
                if (startRoute.value == DashboardDestination) {
                    delay(1000)
                    requestNotificationPermissionForAndroid13()
                }
            }

            NXLeaveTheme {
                if (isAccountEnable) {
                    Scaffold(
                        bottomBar = {
                            AnimatedVisibility(
                                visible = showBottomNav,
                                enter = slideInVertically(initialOffsetY = { it }),
                                exit = slideOutVertically(targetOffsetY = { it })
                            ) {
                                NXBottomNav(
                                    navController = navController,
                                    showApprove = accessLevel !is AccessLevel.None
                                )
                            }
                        },
                        contentWindowInsets = WindowInsets(top = 0, bottom = 0),
                    ) { innerPadding ->
                        CompositionLocalProvider(
                            LocalEntryPadding provides innerPadding,
                        ) {
                            DestinationsNavHost(
                                navGraph = NavGraphs.root,
                                engine = engine,
                                navController = navController,
                                startRoute = startRoute.value
                            )
                        }
                    }
                } else {
                    AccountSuspendContent(
                        onLogout = { viewModel.onLogout() }
                    )
                }
            }
        }
    }

    private fun initPermissionLauncher() {
        notificationPermissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }
    }

    private fun requestNotificationPermissionForAndroid13() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

val LocalEntryPadding = compositionLocalOf {
    PaddingValues()
}

