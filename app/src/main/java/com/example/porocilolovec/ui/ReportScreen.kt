package com.example.porocilolovec.ui


import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.porocilolovec.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import java.io.File
import java.io.IOException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
@SuppressLint("DefaultLocale")
@Composable
fun ReportScreen(viewModel: PorociloLovecViewModel = viewModel(), navController: NavController) {

    // Get the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    // Get the context using LocalContext
    val context = LocalContext.current

    var isButtonActive by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf<Date?>(null) }
    var elapsedMinutes by remember { mutableStateOf(0) }
    var fromText by remember { mutableStateOf("") }
    var toText by remember { mutableStateOf("") }

    fun parseUserInputToElapsedMinutes(input: String): Int {
        val parts = input.split(":").mapNotNull { it.toIntOrNull() }
        return when (parts.size) {
            2 -> parts[0] * 60 + parts[1]
            else -> 0
        }
    }


    val focusRequester = remember { FocusRequester() }

    RequestPermissions(context)

    // Audio Recorder instance
    val audioRecorder = remember { AudioRecorder(context) }

    // Timer logic
    LaunchedEffect(isButtonActive) {
        if (isButtonActive) {
            if (startTime == null) {
                startTime = Date()
                fromText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTime!!)
            }

            while (isButtonActive) {
                delay(60 * 1000L) // Update every minute
                elapsedMinutes += 1
                val hours = elapsedMinutes / 60
                val minutes = elapsedMinutes % 60
                toText = "Trajanje: $hours ur $minutes min"
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (isRecording) { //Ustavi snemanje
                        isRecording = false
                        audioRecorder.stopRecording()           // naloži audio
                        audioFile = audioRecorder.getAudioFile() // Pridobi datoteko
                        Toast.makeText(context, "Snemanje ustavljeno", Toast.LENGTH_SHORT).show()
                    } else {
                        isRecording = true      // Začni snemanje
                        audioRecorder.startRecording()
                        Toast.makeText(context, "Snemanje začeto", Toast.LENGTH_SHORT).show()
                    }
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp), // Zmanjšamo notranji odmik gumba
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.mikrofon), // Nadomesti z vašim ID ikone
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = if (isRecording) stringResource(R.string.btn_stop_recording) else stringResource(R.string.btn_start_recording),
                    maxLines = 2, // Dovolimo 2 vrstici za besedilo
                    textAlign = TextAlign.Center
                )
            }

            if (audioFile != null) {
                Spacer(modifier = Modifier.width(8.dp)) // Razmik med gumbi
                Button(
                    onClick = {
                        convertAudioToText(audioFile!!)
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Audio → Tekst",
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        // Predvajaj posneti zvok
                        audioFile?.let {
                            playAudioFile(it, context) // Play the recorded audio
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Poslušaj Audio",
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = fromText,
                onValueChange = {
                    if (!isButtonActive) {
                        fromText = it
                        elapsedMinutes = parseUserInputToElapsedMinutes(it)
                    }
                },
                label = { Text("Od ure:") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                )
            )

            OutlinedTextField(
                value = toText,
                onValueChange = {
                    if (!isButtonActive) {
                        toText = it
                    }
                },
                label = { Text("Do ure:") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                )
            )
        }

        Button(
            onClick = {
                isButtonActive = !isButtonActive
                if (!isButtonActive) {
                    toText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                } else {
                    if (fromText.isEmpty()) {
                        fromText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    }
                    startTime = Date(Date().time - elapsedMinutes * 60 * 1000L)
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(if (isButtonActive) "Stop" else "Štopaj")
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
            if (distanceInKm.isNotBlank() && reportText.isNotBlank() /*&& timeText.isNotBlank()*/) {
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






























fun convertAudioToText(audioFile: File) {
    Log.d("milestone", "Starting convertAudioToText()")

    // Ensure that audio file exists
    if (!audioFile.exists() || audioFile.length() == 0L) {
        Log.d("milestone", "Error: Audio file doesn't exist or is empty.")
        return
    }

    Log.d("milestone", "Audio file exists, proceeding with the upload.")

    // Create OkHttpClient instance
    val client = OkHttpClient()

    // Create the request body with the audio file
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart(
            "file",  // Form field for the file
            audioFile.name,  // File name
            audioFile.asRequestBody("audio/wav".toMediaTypeOrNull())  // Content type of the file
        )
        .build()

    Log.d("milestone", "Request body created, preparing to send request.")

    // Prepare the request with all necessary headers (matching your snippet)
    val request = Request.Builder()
        .url("https://real-time-speech-processing-api.p.rapidapi.com/asr?task=transcribe&word_timestamps=false&output=txt&encode=true&language=af")
        .post(requestBody)  // Attach the file as part of the POST request body
        .addHeader("x-rapidapi-key", "f91dfae6bcmshfbf494048f276ddp1a5331jsn670a9c89fe73")  // Your RapidAPI key
        .addHeader("x-rapidapi-host", "real-time-speech-processing-api.p.rapidapi.com")
        .addHeader("Content-Type", "application/x-www-form-urlencoded")  // Keeping this header as per your snippet
        .build()

    Log.d("milestone", "Request prepared, sending request.")

    // Send the request on a separate thread to avoid blocking UI thread
    Thread {
        try {
            val response = client.newCall(request).execute()

            // Check if the response is successful
            if (response.isSuccessful) {
                val responseText = response.body?.string()
                Log.d("milestone", "Success: $responseText")
            } else {
                val errorBody = response.body?.string()
                Log.d("milestone", "Error response: $errorBody")
            }
        } catch (e: Exception) {
            // Catch any exceptions and print stack trace
            e.printStackTrace()
            Log.d("milestone", "Request failed: ${e.message}")
        }
    }.start()
}