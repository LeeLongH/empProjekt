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


enum class Hierarchy {
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
            navController.navigate(Hierarchy.Home.name) {
                popUpTo(Hierarchy.Register.name) { inclusive = true } // Odstrani Register iz niza
            }
        }
    }

    NavHost(
        navController = navController,
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
            LoginRegisterScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = Hierarchy.Logout.name) {
            LogoutScreen(viewModel = viewModel, navController = navController)
        }
    }
}