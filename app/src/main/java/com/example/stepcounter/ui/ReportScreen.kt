package com.example.stepcounter.ui


import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged

@SuppressLint("DefaultLocale")
@Composable
fun ReportScreen(viewModel: StepCounterViewModel = viewModel(), navController: NavController) {
    // Handle registration status (optional)
    val uiState by viewModel.uiState.collectAsState()

    // Get the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    // Get the context using LocalContext
    val context = LocalContext.current

    var timeText by remember { mutableStateOf("") }
    var isButtonActive by remember { mutableStateOf(false) }
    var minutes by remember { mutableStateOf(0) } // Timer value in seconds
    val maxTimeInMinutes = 12 * 60 // 12 hours in minutes
    val focusRequester = remember { FocusRequester() }

    // Timer logic
    LaunchedEffect(isButtonActive) {
        if (isButtonActive) {
            while (isButtonActive && minutes < maxTimeInMinutes) {
                if(minutes == 0)
                    timeText = "štopam: 0 minut"
                delay(60 * 1000L) // Update every second
                minutes += 1
                val hours = minutes / 60
                val remainingMinutes = minutes % 60

                if (hours >= 12) {
                    isButtonActive = false // Stop after 12 hours
                    break
                }

                timeText = "$hours ${
                    when (hours) {
                        1 -> "ura"
                        2 -> "uri"
                        3, 4 -> "ure"
                        else -> "ur"
                    }
                } $remainingMinutes min"
            }
        }
    }
    // Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable {
                // Dismiss the keyboard when clicking outside
                keyboardController?.hide()
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Oddaja poročila",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Textarea for report
        var reportText by remember { mutableStateOf("") }
        OutlinedTextField(
            value = reportText,
            onValueChange = { reportText = it },
            label = { Text("Napisite poročilo") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            singleLine = false,
            maxLines = 100,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            )
        )

        // UI with button and text field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time input (hours and minutes)
            OutlinedTextField(
                value = timeText,
                onValueChange = {
                    if (!isButtonActive) {
                        timeText = it // Allow manual input only when the timer is stopped

                    }
                },
                label = { Text("Čas v urah") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                .focusRequester(focusRequester) // Handles focus
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        // When the TextField is focused (clicked), set the current time
                        val hours = minutes / 60.0 // Convert minutes to hours as a Double
                        timeText = String.format("%.2f", hours) // Format to 2 decimal places
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                enabled = !isButtonActive // Disable editing when timer is active
            )

            // Button to start/stop the timer
            Button(
                onClick = {
                    if (isButtonActive) {
                        // Stop the timer
                        isButtonActive = false
                    } else {
                        // Start the timer
                        isButtonActive = true
                    }
                },
                modifier = Modifier.wrapContentWidth(),
            ) {
                Text(if (isButtonActive) "Zaustavi štopanje" else "Pričetek Štopanja")
            }
        }


        // Distance input (kilometers)
        var distanceInKm by remember { mutableStateOf("") }
        OutlinedTextField(
            value = distanceInKm,
            onValueChange = {
                if (it.length <= 3) {
                    distanceInKm = it
                }
            },
            label = { Text("Razdalja v kilometrih") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            )
        )

        // Button to start voice recording
        Button(
            onClick = {
                // Call voice recording API logic here
                Toast.makeText(context, "Snemanje govora začeto", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Začni snemanje govora")
        }
        Button(onClick = {
            if (distanceInKm.isNotBlank() && reportText.isNotBlank() && timeText.isNotBlank()) {
                // Navigate to the login screen
                navController.navigate("Report")
            } else {
                Toast.makeText(context, "Izpolnite vsa polja", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Poslji Poročilo")
        }
        // Button to view reports
        Button(
            onClick = {
                // Navigate to the report history screen
                navController.navigate("reportHistory")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ogled Poročil")
        }
    }

}

