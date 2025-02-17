package com.example.porocilolovec.ui

import android.content.Context
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.porocilolovec.R


@Composable
fun RegisterScreen(viewModel: PorociloLovecViewModel, navController: NavHostController) {
    // State variables for user input
    var nameInput by remember { mutableStateOf("") }
    var surnameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var professionInput by remember { mutableStateOf("") } // Inicializacija za radio gumbe
    val cuvajText = stringResource(R.string.text_cuvaj)
    val upravljalecText = stringResource(R.string.text_upravljalec_lovisca)

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
            text = stringResource(R.string.title_register),
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
            label = { Text(stringResource(R.string.text_name)) },
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
            label = { Text(stringResource(R.string.text_surname)) },
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
            label = { Text(stringResource(R.string.text_email)) },
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
            label = { Text(stringResource(R.string.text_password)) },
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
                text = stringResource(R.string.text_select_your_profession),
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
                        selected = professionInput == cuvajText,
                        onClick = { professionInput = cuvajText }
                    )
                    Text(
                        text = cuvajText,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Hunter Family option
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = professionInput == upravljalecText ,
                        onClick = { professionInput = upravljalecText  }
                    )
                    Text(
                        text = upravljalecText ,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register button
        Button(onClick = {
            if (isValidName(nameInput) &&
                isValidSurname(surnameInput) &&
                isValidEmail(emailInput) /*&&
                professionInput.isNotBlank()*/
                ){
                    // Call the registerUser function in the ViewModel
                    viewModel.registerUser(0, nameInput, surnameInput, professionInput, emailInput)

                // Save email and password for auto-fill
                saveLoginCredentials(context, emailInput, passwordInput)

                    // Show Toast message after registration
                    Toast.makeText(
                        context,
                        "User Registered: $nameInput $surnameInput, Email: $emailInput, Profession: $professionInput",
                        Toast.LENGTH_LONG
                    ).show()

                    //viewModel.UserStorage.saveUsers(UserStorage(context)) // Shrani posodobljen seznam uporabnikov

                    // Navigate to the login screen
                    navController.navigate("Login")
            } else {
                Toast.makeText(context, "Invalid input somewhere", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = stringResource(R.string.btn_register))
        }
    }
}

fun saveLoginCredentials(context: Context, email: String, password: String) {
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("EMAIL", email)
        putString("PASSWORD", password)
        apply()
    }
}
// Function to validate email
fun isValidEmail(emailInput: String): Boolean {
    /*
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    return email.matches(emailRegex)
    */
    return true
}
fun isValidName(nameInput: String): Boolean {
    //return nameInput.length > 1
    return true
}
fun isValidSurname(surnameInput: String): Boolean {
    //return surnameInput.length > 1
    return true
}
