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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign


@Composable
fun RegisterScreen(viewModel: StepCounterViewModel = viewModel(), navController: NavController) {
    // State variables for user input
    var nameInput by remember { mutableStateOf("") }
    var surnameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var professionInput by remember { mutableStateOf("") } // Inicializacija za radio gumbe

    // Handle registration status (optional)
    val uiState by viewModel.uiState.collectAsState()

    // Get the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    // Get the context using LocalContext
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable {
                // Dismiss the keyboard when clicking outside
                keyboardController?.hide()
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Registracija",
            style = TextStyle(
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                letterSpacing = 0.2.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            textAlign = TextAlign.Center
        )


        // TextField for name input
        OutlinedTextField(
            value = nameInput,
            onValueChange = {
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
                if (it.length <= 15) {
                    surnameInput = it
                }
            },
            label = { Text("Surname") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = emailInput,
            onValueChange = {
                emailInput = it
                // Check email format
                if (!isValidEmail(it)) {
                    Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
                }
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email // Use Email type
            )
        )



        Spacer(modifier = Modifier.height(16.dp))

        // Password input field
        OutlinedTextField(
            value = passwordInput,
            onValueChange = {
                if (it.length <= 20) {
                    passwordInput = it
                }
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = "Kaj si?",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    letterSpacing = 0.2.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            // Radio buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hunter option
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = professionInput == "Hunter",
                        onClick = { professionInput = "Hunter" }
                    )
                    Text(
                        text = "Hunter",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Hunter Family option
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = professionInput == "Hunter Family",
                        onClick = { professionInput = "Hunter Family" }
                    )
                    Text(
                        text = "Hunter Family",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register button
        Button(onClick = {
            if (nameInput.isNotBlank() && surnameInput.isNotBlank() && passwordInput.isNotBlank() && professionInput.isNotBlank()) {
                // Call the registerUser function in the ViewModel
                val uniqueID = "";
                viewModel.registerUser(uniqueID.toString(), nameInput, surnameInput, professionInput, emailInput)

                // Show Toast message after registration
                Toast.makeText(
                    context,
                    "User Registered: $nameInput $surnameInput, Profession: $professionInput",
                    Toast.LENGTH_LONG
                ).show()

                //viewModel.UserStorage.saveUsers(UserStorage(context)) // Shrani posodobljen seznam uporabnikov

                // Navigate to the login screen
                navController.navigate("Login")
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Register")
        }
    }
}

// Function to validate email
fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    return email.matches(emailRegex)
}
