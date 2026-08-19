package com.neohear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neohear.audio.NativeBridge
import com.neohear.reminder.FollowUpReminder
import com.neohear.ui.dashboard.DashboardViewModel
import com.neohear.ui.questionnaire.RiskQuestionnaireScreen
import com.neohear.ui.questionnaire.RiskQuestionnaireViewModel
import com.neohear.ui.referrals.ReferralDetailScreen
import com.neohear.ui.referrals.ReferralsListScreen
import com.neohear.ui.referrals.ReferralsViewModel
import com.neohear.ui.screening.ScreeningFlow
import com.neohear.ui.screening.ScreeningViewModel
import com.neohear.ui.screens.DashboardScreen
import com.neohear.ui.screens.DisclaimerOverlay
import com.neohear.ui.screens.HomeScreen
import com.neohear.ui.screens.SettingsScreen
import com.neohear.ui.screens.CryAnalysisScreen
import com.neohear.ui.cry.CryAnalysisViewModel
import com.neohear.ui.theme.NeoHearTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val pingResult = NativeBridge.ping()

        // Fire referral reminders on launch
        val app = application as NeoHearApp
        val reminder = FollowUpReminder(this, app.database)
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            reminder.checkAndNotify()
        }

        setContent {
            NeoHearTheme {
                    MainScreen(nativePing = pingResult)
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Referrals : Screen("referrals", "Referrals", Icons.Default.People)
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

const val ROUTE_SCREENING = "screening"
const val ROUTE_QUESTIONNAIRE = "questionnaire"
const val ROUTE_REFERRAL_DETAIL = "referral_detail/{referralId}"
const val ROUTE_CRY_ANALYSIS = "cry_analysis"

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Referrals,
    Screen.Dashboard,
    Screen.Settings,
)

/** Routes that should hide the bottom navigation bar. */
private val hideBottomBarRoutes = setOf(
    ROUTE_SCREENING,
    ROUTE_QUESTIONNAIRE,
    ROUTE_REFERRAL_DETAIL,
    ROUTE_CRY_ANALYSIS
)

@Composable
fun MainScreen(nativePing: String) {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val screeningViewModel: ScreeningViewModel = viewModel(
        factory = ScreeningViewModel.Factory(context.applicationContext as android.app.Application)
    )
    val referralsViewModel: ReferralsViewModel = viewModel(
        factory = ReferralsViewModel.Factory(context.applicationContext as android.app.Application)
    )
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.Factory(context.applicationContext as android.app.Application)
    )

    // First-launch disclaimer state
    val prefs = remember {
        context.getSharedPreferences("neohear_prefs", android.content.Context.MODE_PRIVATE)
    }
    var disclaimerAccepted by remember {
        mutableStateOf(prefs.getBoolean("disclaimer_accepted", false))
    }

    if (!disclaimerAccepted) {
        DisclaimerOverlay(
            onAccept = {
                prefs.edit().putBoolean("disclaimer_accepted", true).apply()
                disclaimerAccepted = true
            }
        )
        return
    }

    val isDemoMode by screeningViewModel.isDemoMode.collectAsState()

    // Connectivity + sync manager for Data & Sync UI
    val connectivityMonitor = com.neohear.sync.ConnectivityMonitor.getInstance(context.applicationContext as android.app.Application)
    val connectivityState by connectivityMonitor.state.collectAsState()
    var pendingSyncCount by remember { mutableStateOf(0) }
    var totalSyncRecords by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val mgr = com.neohear.sync.SyncManager.getInstance(context.applicationContext as android.app.Application)
        pendingSyncCount = mgr.getPendingCount()
        totalSyncRecords = mgr.getTotalSyncRecords()
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute !in hideBottomBarRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    isDemoMode = isDemoMode,
                    onStartScreening = {
                        screeningViewModel.startNewScreening()
                        navController.navigate(ROUTE_SCREENING)
                    },
                    onStartQuestionnaire = {
                        navController.navigate(ROUTE_QUESTIONNAIRE)
                    },
                    onStartCryAnalysis = {
                        navController.navigate(ROUTE_CRY_ANALYSIS)
                    }
                )
            }
            composable(Screen.Referrals.route) {
                ReferralsListScreen(
                    viewModel = referralsViewModel,
                    isDemoMode = isDemoMode,
                    onReferralClick = { referralId ->
                        navController.navigate("referral_detail/$referralId")
                    },
                    onLogFollowUp = { referralId ->
                        navController.navigate("referral_detail/$referralId")
                    }
                )
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(viewModel = dashboardViewModel, isDemoMode = isDemoMode)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    isDemoMode = isDemoMode,
                    onDemoModeToggle = { screeningViewModel.setDemoMode(it) },
                    onRunDemoScenario = { scenarioId ->
                        screeningViewModel.startDemoScenario(scenarioId)
                        // navigate to screening
                        navController.navigate(ROUTE_SCREENING)
                    },
                    connectivityState = connectivityState,
                    pendingSyncCount = pendingSyncCount,
                    totalSyncRecords = totalSyncRecords,
                    onSimulateSync = {
                        // run simulated sync (success)
                        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                            val mgr = com.neohear.sync.SyncManager.getInstance(context.applicationContext as android.app.Application)
                            mgr.simulateSync(true)
                            pendingSyncCount = mgr.getPendingCount()
                            totalSyncRecords = mgr.getTotalSyncRecords()
                        }
                    },
                    onSimulateFail = {
                        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                            val mgr = com.neohear.sync.SyncManager.getInstance(context.applicationContext as android.app.Application)
                            mgr.simulateSync(false)
                            pendingSyncCount = mgr.getPendingCount()
                            totalSyncRecords = mgr.getTotalSyncRecords()
                        }
                    }
                )
            }
            composable(ROUTE_SCREENING) {
                ScreeningFlow(
                    viewModel = screeningViewModel,
                    onNavigateToQuestionnaire = {
                        navController.navigate(ROUTE_QUESTIONNAIRE)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }
            composable(ROUTE_QUESTIONNAIRE) {
                val questionnaireViewModel: RiskQuestionnaireViewModel = viewModel()
                RiskQuestionnaireScreen(
                    viewModel = questionnaireViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(ROUTE_CRY_ANALYSIS) {
                val cryAnalysisViewModel: CryAnalysisViewModel = viewModel(
                    factory = CryAnalysisViewModel.Factory(
                        context.applicationContext as android.app.Application
                    )
                )
                CryAnalysisScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = cryAnalysisViewModel
                )
            }
            composable(
                route = ROUTE_REFERRAL_DETAIL,
                arguments = listOf(
                    navArgument("referralId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val referralId = backStackEntry.arguments?.getString("referralId") ?: return@composable
                LaunchedEffect(referralId) {
                    referralsViewModel.loadReferralDetail(java.util.UUID.fromString(referralId))
                }
                ReferralDetailScreen(
                    viewModel = referralsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
