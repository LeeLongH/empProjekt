package com.example.stepcounter.ui


import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.stepcounter.R
import com.example.stepcounter.ui.StepCounterViewModel
import java.io.File
import java.io.IOException

@Suppress("DEPRECATION")
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
    var minutes by remember { mutableIntStateOf(0) } // Timer value in seconds
    val maxTimeInMinutes = 12 * 60 // 12 hours in minutes
    val focusRequester = remember { FocusRequester() }

    RequestPermissions(context)

    // Audio Recorder instance
    val audioRecorder = remember { AudioRecorder(context) }

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
                    minutes = 0
                    timeText = ""
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
                keyboardController?.hide()
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.title_submit_report),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        var reportText by remember { mutableStateOf("") }
        OutlinedTextField(
            value = reportText,
            onValueChange = { reportText = it },
            label = { Text(stringResource(R.string.text_write_report)) },
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

        // Button to start voice recording
        var isRecording by remember { mutableStateOf(false) }
        var audioFile: File? by remember { mutableStateOf(null) }

        Button(
            onClick = {
                if (isRecording) {
                    // Stop recording and upload audio
                    isRecording = false
                    audioRecorder.stopRecording()


                    // Get the audio file after stopping the recording
                    val audioFile = audioRecorder.getAudioFile()

                    // Play the audio if the file exists
                    audioFile?.let {
                        playAudioFile(it, context) // Play the recorded audio
                    }


                    Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
                } else {
                    // Start recording
                    isRecording = true
                    // Implement recording logic here to create a file
                    audioRecorder.startRecording()
                    Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.mikrofon), // Replace with your icon
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = if (isRecording) stringResource(R.string.btn_stop_recording) else stringResource(R.string.btn_start_recording)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = timeText,
                onValueChange = {
                    if (!isButtonActive) {
                        timeText = it // Allow manual input only when the timer is stopped

                    }
                },
                label = { Text(stringResource(R.string.text_time_input)) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                .focusRequester(focusRequester) // Handles focus
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        val hours = minutes / 60.0
                        timeText = String.format("%.2f", hours)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                enabled = !isButtonActive
            )

            Button(
                onClick = {
                    isButtonActive = !isButtonActive
                },
                modifier = Modifier.wrapContentWidth(),
            ) {
                Text(
                    text = if (isButtonActive) { stringResource(R.string.btn_stop_timing)
                    } else { stringResource(R.string.btn_start_timing) }
                )
            }
        }

        var distanceInKm by remember { mutableStateOf("") }
        OutlinedTextField(
            value = distanceInKm,
            onValueChange = {
                if (it.length <= 3) {
                    distanceInKm = it
                }
            },
            label = { Text(stringResource(R.string.text_distance)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            )
        )

        Button(onClick = {
            if (distanceInKm.isNotBlank() && reportText.isNotBlank() && timeText.isNotBlank()) {
                navController.navigate("Report")
            } else { Toast.makeText(context, "Izpolnite vsa polja", Toast.LENGTH_SHORT).show() }
        }) { Text(stringResource(R.string.btn_submit_report)) }

        Button(
            onClick = { navController.navigate("History") }, modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.btn_view_reports)) }

        Button(
            onClick = { navController.navigate("Home") }, modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.btn_home_screen)) }
    }

}
@Composable
fun RequestPermissions(context: Context) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }
}


fun playAudioFile(audioFile: File, context: Context) {
    val mediaPlayer = MediaPlayer()
    try {
        mediaPlayer.setDataSource(audioFile.absolutePath)
        mediaPlayer.prepare() // Pripravi predvajanje
        mediaPlayer.start() // Začni predvajanje
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Error playing audio: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}


