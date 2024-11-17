package com.example.stepcounter.ui

import StepCounterViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stepcounter.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.RadioButton
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*


@Composable
fun RegisterScreen(viewModel: StepCounterViewModel = viewModel(), navController: NavController) {
    // State variables for user input
    var nameInput by remember { mutableStateOf("") }
    var surnameInput by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf("") }

    // Handle registration status (optional)
    val uiState by viewModel.uiState.collectAsState()

    // Get the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    // Get the context using LocalContext
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).clickable {
            // Dismiss the keyboard when clicking outside
            keyboardController?.hide()
        },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TextField for name input
        OutlinedTextField(
            value = nameInput,
            onValueChange = {
                // Limit name input to 15 characters
                if (it.length <= 15) {
                    nameInput = it
                }
            },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TextField for surname input
        OutlinedTextField(
            value = surnameInput,
            onValueChange = {
                // Limit surname input to 15 characters
                if (it.length <= 15) {
                    surnameInput = it
                }
            },
            label = { Text("Surname") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TextField for selecting profession (replace with your own selection UI)
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { selectedOption = it },
            label = { Text("Select Profession") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Register button
        Button(onClick = {
            if (nameInput.isNotBlank() && surnameInput.isNotBlank() && selectedOption.isNotBlank()) {
                // Call the registerUser function in the ViewModel
                viewModel.registerUser(nameInput, surnameInput, selectedOption)

                // Show Toast message after registration
                Toast.makeText(
                    context,
                    "User Registered: $nameInput $surnameInput, Profession: $selectedOption",
                    Toast.LENGTH_LONG
                ).show()

                // Navigate back after registration (optional)
                //navController.navigateUp()
                navController.navigate("Login") // Replace "login" with the correct route for your login screen
            }
        }) {
            Text(text = "Register")
        }
    }
}