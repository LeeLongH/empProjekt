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

        var workRequests by remember { mutableStateOf<String?>(null) }

        // Fetch work requests when the Composable is first launched
        LaunchedEffect(Unit) {
            // Call getWorkRequests and handle success/failure in the callback
            viewModel.getWorkRequests(context) { result ->
                workRequests = result
            }
        }

        // UI
        Column(modifier = Modifier.padding(16.dp)) {
            if (!workRequests.isNullOrEmpty()) {  // Show button if workRequests is not empty
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    navController.navigate(Hierarchy.WorkRequests.name)  // Navigate to home
                }) {
                    Text(text = "Accept Work Requests")
                }
            } else {
                // Optionally show some text when no work requests are available
                Text(text = "No work requests available.")
            }
        }


    }
}
