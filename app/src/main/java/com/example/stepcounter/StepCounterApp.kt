package com.example.stepcounter

import StepCounterViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stepcounter.ui.GoalScreen
import com.example.stepcounter.ui.HistoryScreen
import com.example.stepcounter.ui.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.stepcounter.ui.LoginScreen
import com.example.stepcounter.ui.RegisterScreen

enum class StepCounterScreen() {
    Home,
    Goal,
    History,
    Register,
    Login,
}

@Composable
fun StepCounterApp(viewModel: StepCounterViewModel = viewModel(),
                   navController: NavHostController = rememberNavController()) {

    NavHost(navController = navController,
        startDestination = StepCounterScreen.Register.name
    ) {
        composable(route = StepCounterScreen.Home.name) {
            HomeScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = StepCounterScreen.Goal.name) {
            GoalScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = StepCounterScreen.History.name) {
            HistoryScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = StepCounterScreen.Register.name) {
            RegisterScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = StepCounterScreen.Login.name) {
            LoginScreen(viewModel = viewModel, navController = navController)
        }
    }
}