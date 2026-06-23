package com.urbanvoice.app.presentation.navigation

import androidx.compose.runtime.Composable
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
import com.urbanvoice.app.presentation.ui.profile.ProfileScreen
import com.urbanvoice.app.presentation.ui.report.ReportIncidentScreen
import com.urbanvoice.app.presentation.ui.reports.IncidentDetailScreen
import com.urbanvoice.app.presentation.ui.reports.MyReportsScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val REPORT_INCIDENT = "report_incident"
    const val MY_REPORTS = "my_reports"
    const val INCIDENT_DETAIL = "incident_detail/{reportId}"
    const val ALERTS = "alerts"
    const val PROFILE = "profile"
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(onNavigateToRegister = {
                navController.navigate(Routes.REGISTER)
            }, onNavigateToHome = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            })
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
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
                onNavigateToReport = { navController.navigate(Routes.REPORT_INCIDENT) },
                onNavigateToAlerts = { navController.navigate(Routes.ALERTS) },
                onNavigateToMyReports = { navController.navigate(Routes.MY_REPORTS) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onNavigateToDetail = { reportId ->
                    navController.navigate("incident_detail/$reportId")
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.REPORT_INCIDENT) {
            ReportIncidentScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.popBackStack() }
            )
        }
        composable(Routes.MY_REPORTS) {
            MyReportsScreen(
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
            AlertsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
