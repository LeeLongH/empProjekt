package com.example.porocilolovec

import android.content.Context
import android.util.Log
import com.example.porocilolovec.ui.PorociloLovecViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController




import com.example.porocilolovec.ui.RegisterScreen
import com.example.porocilolovec.ui.LoginScreen
import com.example.porocilolovec.ui.HomeScreen

import com.example.porocilolovec.ui.SearchUsersByProfessionScreen

/*

import com.example.porocilolovec.ui.HistoryScreen
import com.example.porocilolovec.ui.LoginRegisterLogoutScreen

import com.example.porocilolovec.ui.ReportScreen
import com.example.porocilolovec.ui.WorkRequestsScreen
import com.example.porocilolovec.ui.ManagerReportScreen
*/
enum class Hierarchy {
    Register,
    Login,
    LoginRegister,
    Home,
    Search,
    Report,
    History,
    WorkRequests,
    ManagerReportView
}

@Composable
fun PorociloLovecApp(
    viewModel: PorociloLovecViewModel = viewModel(), // Get ViewModel
    navController: NavHostController = rememberNavController()
) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val savedEmail = sharedPreferences.getString("USER_EMAIL", null)
    val savedPassword = sharedPreferences.getString("USER_PASSWORD", null)

    /*LaunchedEffect(savedEmail, savedPassword) {
        if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            val user = viewModel.getUserByEmailAndPassword(savedEmail, savedPassword) // Check DB
            if (user != null) {
                Log.d("PorociloLovecApp", "User found: ${user.fullName}, navigating to Home")
                navController.navigate(Hierarchy.Home.name) {
                    popUpTo(0) // Clear back stack
                }
            } else {
                // User does not exist -> clear SharedPreferences
                sharedPreferences.edit().clear().apply()
                navController.navigate(Hierarchy.Register.name)
            }
        }
    }*/

    NavHost(
        navController = navController,
        //startDestination = if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) Hierarchy.Home.name else Hierarchy.Register.name
        startDestination = Hierarchy.Register.name
        ) {
        composable(route = Hierarchy.Home.name) {
            HomeScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = Hierarchy.Register.name) {
            RegisterScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = Hierarchy.Login.name) {
            LoginScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = Hierarchy.Report.name) {
            ReportScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = Hierarchy.History.name) {
            HistoryScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = Hierarchy.Search.name) {
            SearchUsersByProfessionScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = Hierarchy.LoginRegister.name) {
            LoginRegisterLogoutScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = Hierarchy.WorkRequests.name) {
            WorkRequestsScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = Hierarchy.ManagerReportView.name) {
            ManagerReportScreen(viewModel = viewModel, navController = navController)
        }
    }
}

@Composable
fun ManagerReportScreen(viewModel: PorociloLovecViewModel, navController: NavHostController) {
    TODO("Not yet implemented")
}

@Composable
fun WorkRequestsScreen(viewModel: PorociloLovecViewModel, navController: NavHostController) {
    TODO("Not yet implemented")
}

@Composable
fun LoginRegisterLogoutScreen(viewModel: PorociloLovecViewModel, navController: NavHostController) {
    TODO("Not yet implemented")
}

@Composable
fun HistoryScreen(viewModel: PorociloLovecViewModel, navController: NavHostController) {
    TODO("Not yet implemented")
}

@Composable
fun ReportScreen(viewModel: PorociloLovecViewModel, navController: NavHostController) {
    TODO("Not yet implemented")
}
