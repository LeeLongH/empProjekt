package com.example.porocilolovec

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
import com.example.porocilolovec.ui.LoginRegisterScreen
import com.example.porocilolovec.ui.LoginScreen
import com.example.porocilolovec.ui.RegisterScreen
import com.example.porocilolovec.ui.ReportScreen
import com.example.porocilolovec.ui.SearchUsersByProfessionScreen
import com.example.porocilolovec.ui.LogoutScreen


enum class StepCounterScreen {
    Register,
    Login,
    LoginRegister,
    Home,
    Search,
    Report,
    History,
    Logout,
}

@Composable
fun StepCounterApp(
    viewModel: PorociloLovecViewModel = viewModel(), // Pridobimo ViewModel
    navController: NavHostController = rememberNavController()
) {

    val context = LocalContext.current

    // Preveri, če je uporabnik prijavljen
    val isUserLoggedIn = remember {
        viewModel.isUserLoggedIn(context)
    }

    // Preusmeri na domači zaslon, če je uporabnik že prijavljen
    LaunchedEffect(isUserLoggedIn) {
        if (isUserLoggedIn) {
            navController.navigate(StepCounterScreen.Home.name) {
                popUpTo(StepCounterScreen.Register.name) { inclusive = true } // Odstrani Register iz niza
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = StepCounterScreen.Register.name
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
        composable(route = StepCounterScreen.LoginRegister.name) {
            LoginRegisterScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = StepCounterScreen.Logout.name) {
            LogoutScreen(viewModel = viewModel, navController = navController)
        }
    }
}