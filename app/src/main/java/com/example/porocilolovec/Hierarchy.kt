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
import com.example.porocilolovec.ui.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.porocilolovec.ui.HistoryScreen
import com.example.porocilolovec.ui.LoginRegisterLogoutScreen
import com.example.porocilolovec.ui.LoginScreen
import com.example.porocilolovec.ui.RegisterScreen
import com.example.porocilolovec.ui.ReportScreen
import com.example.porocilolovec.ui.SearchUsersByProfessionScreen
import com.example.porocilolovec.ui.WorkRequestsScreen

enum class Hierarchy {
    Register,
    Login,
    LoginRegister,
    Home,
    Search,
    Report,
    History,
    workRequests,
}

@Composable
fun PorociloLovecApp(
    viewModel: PorociloLovecViewModel = viewModel(), // Get ViewModel
    navController: NavHostController = rememberNavController()
) {

    val context = LocalContext.current

    // ✅ Use the correct SharedPreferences keys
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val savedEmail = sharedPreferences.getString("USER_EMAIL", null)
    val savedPassword = sharedPreferences.getString("USER_PASSWORD", null)

    // ✅ Navigate directly to Home if user is already logged in
    LaunchedEffect(savedEmail, savedPassword) {
        if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            Log.d("PorociloLovecApp", "Auto-login with saved credentials: Email = $savedEmail")
            navController.navigate(Hierarchy.Home.name) {
                popUpTo(0) // Clear the back stack
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (savedEmail != null && savedPassword != null) Hierarchy.Home.name else Hierarchy.Register.name
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
        composable(route = Hierarchy.workRequests.name) {
            WorkRequestsScreen(viewModel = viewModel, navController = navController)
        }



    }
}