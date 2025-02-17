package com.example.porocilolovec.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.porocilolovec.StepCounterScreen
import com.example.porocilolovec.R

@Composable
fun LogoutScreen(viewModel: PorociloLovecViewModel = viewModel(),
                 navController: NavController
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 24.sp,
            //fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { navController.navigate(StepCounterScreen.LoginRegister.name) }) {
            Text(text = stringResource(R.string.btn_home_screen))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showDialog = true }) {
            Text(text = stringResource(R.string.btn_logout))
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(R.string.logout_confirmation)) },
            text = { Text(text = stringResource(R.string.logout_message)) },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate(StepCounterScreen.Login.name) {
                        popUpTo(0) // Clears backstack
                    }
                }) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}