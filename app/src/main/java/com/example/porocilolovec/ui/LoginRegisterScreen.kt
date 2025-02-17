package com.example.porocilolovec.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.porocilolovec.R



@Composable
fun LoginRegisterScreen(viewModel: PorociloLovecViewModel = viewModel(), navController: NavController){

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
            }
        ) {
            Text(text = stringResource(R.string.btn_login))
        }

        // Register button
        Button(onClick = {
            navController.navigate("Register")
        }
        ) {
            Text(text = stringResource(R.string.btn_register))
        }

        // Logout button
        Button(onClick = {
            navController.navigate("Logout")
        }
        ) {
            Text(text = stringResource(R.string.btn_logout))
        }

    }
}

