package com.example.porocilolovec.ui

import android.content.Context
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

    // Fetch current user data on screen load
    LaunchedEffect(Unit) {
        viewModel.getCurrentUserId(context) // Fetch user ID
        viewModel.getCurrentUserProfession(context) { profession ->
            // Handle the profession result here
            // The profession is updated in the ViewModel, so you can observe it
            Log.d("HomeScreen", "User profession: $profession")
        }
    }

    // Observe the currentUserId and userProfession
    val currentUserId = viewModel.currentUserId.collectAsState().value
    val userProfession = viewModel.currentUserProfession.collectAsState().value
    val workRequests = viewModel.workRequests.collectAsState().value // Collecting work requests

    // Check if there are work requests and display a button for accepting
    val hasWorkRequests = workRequests.isNotEmpty()

    // UI to display the user ID, profession and additional buttons
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Current User ID: $currentUserId",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "User Profession: ${userProfession ?: "Unknown"}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Conditionally show buttons based on profession
        when (userProfession) {
            "lovec" -> {
                Button(onClick = { navController.navigate(Hierarchy.Report.name) }) {
                    Text(text = stringResource(R.string.text_write_report))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navController.navigate(Hierarchy.History.name) }) {
                    Text(text = stringResource(R.string.title_history_report))
                }
            }
            "staresina", "gospodar" -> {
                Button(onClick = { navController.navigate(Hierarchy.ManagerReportView.name) }) {
                    Text(text = "Ogled poročil")
                }
            }
        }

        // If there are work requests, show a button to accept work requests
        if (hasWorkRequests) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate(Hierarchy.Home.name) }) {
                Text(text = "Accept Work Requests")
            }
        }
    }
}
