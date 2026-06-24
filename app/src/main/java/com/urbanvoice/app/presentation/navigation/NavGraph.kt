package com.urbanvoice.app.presentation.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.urbanvoice.app.presentation.ui.alert.AlertsScreen
import com.urbanvoice.app.presentation.ui.auth.LoginScreen
import com.urbanvoice.app.presentation.ui.auth.RegisterScreen
import com.urbanvoice.app.presentation.ui.home.HomeScreen
import com.urbanvoice.app.presentation.ui.moderate.ModerationScreen
import com.urbanvoice.app.presentation.ui.profile.ProfileScreen
import com.urbanvoice.app.presentation.ui.report.ReportIncidentScreen
import com.urbanvoice.app.presentation.ui.reports.IncidentDetailScreen
import com.urbanvoice.app.presentation.ui.reports.MyReportsScreen
import com.urbanvoice.app.presentation.viewmodel.AuthViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val REPORT_INCIDENT = "report_incident"
    const val MY_REPORTS = "my_reports"
    const val INCIDENT_DETAIL = "incident_detail/{reportId}"
    const val ALERTS = "alerts"
    const val PROFILE = "profile"
    const val MODERATE = "moderate"
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val activity = LocalContext.current as ComponentActivity
    val authViewModel: AuthViewModel = hiltViewModel(activity)

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                authViewModel = authViewModel,
                onNavigateToReport = { navController.navigate(Routes.REPORT_INCIDENT) },
                onNavigateToAlerts = { navController.navigate(Routes.ALERTS) },
                onNavigateToMyReports = { navController.navigate(Routes.MY_REPORTS) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onNavigateToModerate = { navController.navigate(Routes.MODERATE) },
                onNavigateToDetail = { reportId ->
                    navController.navigate("incident_detail/$reportId")
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.REPORT_INCIDENT) {
            ReportIncidentScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.popBackStack() }
            )
        }
        composable(Routes.MY_REPORTS) {
            MyReportsScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { reportId ->
                    navController.navigate("incident_detail/$reportId")
                }
            )
        }
        composable(
            route = Routes.INCIDENT_DETAIL,
            arguments = listOf(navArgument("reportId") { type = NavType.IntType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getInt("reportId") ?: return@composable
            IncidentDetailScreen(reportId = reportId, onNavigateBack = { navController.popBackStack() })
        }
        composable(Routes.ALERTS) {
            AlertsScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MODERATE) {
            ModerationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { reportId ->
                    navController.navigate("incident_detail/$reportId")
                }
            )
        }
    }
}
