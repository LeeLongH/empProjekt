package com.example.porocilolovec.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.porocilolovec.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun LoginRegisterLogoutScreen(viewModel: PorociloLovecViewModel = viewModel(), navController: NavController) {

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // Login button
        Button(onClick = {
            navController.navigate("Login")
        }) {
            Text("login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register button
        Button(onClick = {
            navController.navigate("Register")
        }) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout button
        Button(onClick = {
            showDialog = true
        }) {
            Text("logout")
        }
    }

    // Confirmation dialog for logout
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("logout") },
            text = { Text("soue you want to logout") },
            confirmButton = {
                Button(onClick = {
                    // Clear session data
                    viewModel.clearUserData(navController.context)

                    // Close the dialog and navigate to the login screen
                    showDialog = false
                    navController.navigate("Login") {
                        popUpTo(0) // Clears the back stack so the user can't go back
                    }
                }) {
                    Text("logout")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("cancel")
                }
            }
        )
    }

}
