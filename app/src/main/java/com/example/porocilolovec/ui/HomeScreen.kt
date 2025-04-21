package com.example.porocilolovec.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.porocilolovec.R
import com.example.porocilolovec.Hierarchy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
@Composable
fun HomeScreen(viewModel: PorociloLovecViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current

    // Fetch user data directly from SharedPreferences
    val currentUserId = viewModel.getUserIdFromPrefs(context)
    val currentUserFullName = viewModel.getUserFullNameFromPrefs(context)
    val userProfession = viewModel.getUserProfessionFromPrefs(context)

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Full Name: $currentUserFullName",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "User ID: $currentUserId",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "User Profession: $userProfession",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (userProfession) {
            "Hunter" -> {
                Button(onClick = { navController.navigate(Hierarchy.Report.name) }) {
                    Text("write report")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navController.navigate(Hierarchy.History.name) }) {
                    Text("report history")
                }
            }

            "Patron", "Sage" -> {
                Button(onClick = { navController.navigate(Hierarchy.ManagerReportView.name) }) {
                    Text(text = "view reports")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(Hierarchy.LoginRegister.name)
        }) {
            Text(text = "Register/Login/Logout")
        }
    }
}
