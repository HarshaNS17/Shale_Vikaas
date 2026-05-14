package com.example.shaalevikaas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shaalevikaas.ui.components.BottomNavigationBar
import com.example.shaalevikaas.ui.screens.*
import com.example.shaalevikaas.ui.theme.ShaaleVikaasTheme
import com.example.shaalevikaas.viewmodel.AuthViewModel
import com.example.shaalevikaas.viewmodel.NeedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShaaleVikaasTheme {
                ShaaleVikasApp()
            }
        }
    }
}

@Composable
fun ShaaleVikasApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val needViewModel: NeedViewModel = hiltViewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val userState by authViewModel.userState.collectAsState()
    val isAdmin = authViewModel.isAdmin()

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf("alumni_dashboard", "needs_list", "hall_of_fame", "announcements", "profile")) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(onTimeout = {
                    val startDest = when {
                        authViewModel.isUserLoggedIn() -> {
                            if (isAdmin) "admin_dashboard" else "alumni_dashboard"
                        }
                        else -> "role_selection"
                    }
                    navController.navigate(startDest) {
                        popUpTo("splash") { inclusive = true }
                    }
                })
            }
            composable("role_selection") {
                RoleSelectionScreen(
                    onAdminClick = { navController.navigate("admin_login") },
                    onAlumniClick = { navController.navigate("alumni_login") }
                )
            }
            composable("admin_login") {
                LoginScreen(
                    role = "Admin",
                    viewModel = authViewModel,
                    onLoginSuccess = { navController.navigate("admin_dashboard") { popUpTo("role_selection") { inclusive = true } } },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("alumni_login") {
                LoginScreen(
                    role = "Alumni",
                    viewModel = authViewModel,
                    onLoginSuccess = { navController.navigate("alumni_dashboard") { popUpTo("role_selection") { inclusive = true } } },
                    onRegisterClick = { navController.navigate("alumni_register") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("alumni_register") {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = { navController.navigate("alumni_dashboard") { popUpTo("role_selection") { inclusive = true } } },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("admin_dashboard") {
                AdminDashboard(
                    viewModel = needViewModel,
                    onAddNeed = { navController.navigate("add_edit_need") },
                    onEditNeed = { id -> navController.navigate("add_edit_need?needId=$id") },
                    onViewPledges = { navController.navigate("pledge_list") },
                    onViewAlumni = { navController.navigate("alumni_list") },
                    onAddAnnouncement = { navController.navigate("add_announcement") },
                    onLogout = { authViewModel.logout { navController.navigate("role_selection") { popUpTo(0) } } }
                )
            }
            composable("alumni_list") {
                AlumniListScreen(viewModel = needViewModel, onBack = { navController.popBackStack() })
            }
            composable("pledge_list") {
                PledgeListScreen(viewModel = needViewModel, onBack = { navController.popBackStack() })
            }
            composable("add_announcement") {
                AddAnnouncementScreen(viewModel = needViewModel, onBack = { navController.popBackStack() })
            }
            composable("alumni_dashboard") {
                AlumniDashboard(
                    viewModel = needViewModel,
                    authViewModel = authViewModel,
                    onNeedClick = { id -> navController.navigate("need_details/$id") }
                )
            }
            composable("needs_list") {
                NeedsListScreen(
                    viewModel = needViewModel,
                    onNeedClick = { id -> navController.navigate("need_details/$id") }
                )
            }
            composable("hall_of_fame") {
                HallOfFameScreen(viewModel = needViewModel)
            }
            composable("announcements") {
                AnnouncementScreen(viewModel = needViewModel)
            }
            composable("profile") {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onLogout = { authViewModel.logout { navController.navigate("role_selection") { popUpTo(0) } } }
                )
            }
            composable(
                route = "need_details/{needId}",
                arguments = listOf(navArgument("needId") { type = NavType.StringType })
            ) { backStackEntry ->
                val needId = backStackEntry.arguments?.getString("needId") ?: ""
                NeedDetailsScreen(
                    needId = needId,
                    viewModel = needViewModel,
                    authViewModel = authViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "add_edit_need?needId={needId}",
                arguments = listOf(navArgument("needId") { defaultValue = null; nullable = true; type = NavType.StringType })
            ) { backStackEntry ->
                val needId = backStackEntry.arguments?.getString("needId")
                AddEditNeedScreen(
                    needId = needId,
                    viewModel = needViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
