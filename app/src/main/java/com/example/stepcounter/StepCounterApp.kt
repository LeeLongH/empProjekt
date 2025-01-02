package com.example.stepcounter

import com.example.stepcounter.ui.StepCounterViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stepcounter.ui.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.stepcounter.ui.HistoryScreen
import com.example.stepcounter.ui.LoginScreen
import com.example.stepcounter.ui.RegisterScreen
import com.example.stepcounter.ui.ReportScreen
import com.example.stepcounter.ui.SearchUsersByProfessionScreen

enum class StepCounterScreen() {
    Home,
    Goal,
    Register,
    Login,
    Report,
    History,
    Search
}
@Composable
fun StepCounterApp(
    viewModel: StepCounterViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = StepCounterScreen.Search.name
    ) {
        composable(route = StepCounterScreen.Home.name) {
            HomeScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = StepCounterScreen.Register.name) {
            RegisterScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = StepCounterScreen.Login.name) {
            LoginScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = StepCounterScreen.Report.name) {
            ReportScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = StepCounterScreen.History.name) {
            HistoryScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = StepCounterScreen.Search.name) {
            SearchUsersByProfessionScreen(viewModel = viewModel, navController = navController)
        }

    }
}
