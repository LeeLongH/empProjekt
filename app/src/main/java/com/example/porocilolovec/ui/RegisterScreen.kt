package com.example.porocilolovec.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController




@Composable
fun RegisterScreen(viewModel: PorociloLovecViewModel = viewModel(), navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Register", fontSize = 30.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Row {
            Row {
                RadioButton(selected = profession == "lovec", onClick = { profession = "lovec" })
                Text(text = "Lovec")
            }
            Row {
                RadioButton(selected = profession == "gospodar", onClick = { profession = "gospodar" })
                Text(text = "Gospodar")
            }
            Row {
                RadioButton(selected = profession == "staresina", onClick = { profession = "staresina" })
                Text(text = "Staresina")
            }
        }

        Button(onClick = {
            if (fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank() && profession.isNotBlank()) {
                val user = User(fullName = fullName, email = email, password = password, profession = profession)

                viewModel.registerUser(user)
                viewModel.saveUserData(context, user)

                Toast.makeText(context, "User Registered!", Toast.LENGTH_SHORT).show()
                if (profession == "lovec")
                    navController.navigate("Search")
                else
                    navController.navigate("Home")
            } else {
                Toast.makeText(context, "Fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Register")
        }

        Button(onClick = { navController.navigate("Login") }) {
            Text("Go to Login")
        }

        Button(onClick = { viewModel.clearUserData(context) }) {
            Text("Clear User Data")
        }
    }
}