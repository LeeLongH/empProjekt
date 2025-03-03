package com.example.porocilolovec.ui


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.porocilolovec.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@SuppressLint("DefaultLocale")
@Composable
fun ReportScreen(viewModel: PorociloLovecViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isButtonActive by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf<Date?>(null) }
    var elapsedMinutes by remember { mutableIntStateOf(0) }
    var fromText by remember { mutableStateOf("") }
    var toText by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }

    RequestPermissions(context)

    // Funkcija za začetek/ustavitev timerja
    fun startStopTimer() {
        if (!isButtonActive) {
            // Začetek timerja
            isButtonActive = true
            // Preverimo, ali je "Od ura" že nastavljena
            if (fromText.isBlank()) {
                // Če ni nastavljena, nastavimo trenutni čas
                startTime = Date() // Nastavi začetni čas
                fromText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTime!!)
            }
            durationText = "Trajanje: 0 ur 0 min"
        } else {
            // Ustavitev timerja
            isButtonActive = false
            val endTime = Date()
            elapsedMinutes = ((endTime.time - startTime!!.time) / (1000 * 60)).toInt() // Izračunaj pretečeni čas v minutah
            val hours = elapsedMinutes / 60
            val minutes = elapsedMinutes % 60
            toText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(endTime)

            // Če "do ura" ni bilo izpolnjeno, se nastavi s trenutnim časom
            if (toText.isBlank()) {
                toText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(endTime)
            }
            // Izračunaj in prikaži trajanje
            durationText = "Trajanje: $hours ur $minutes min"
        }
    }

    // Posodobitev trajanja ob spremembi "Od ura" ali "Do ura"
    fun updateDuration() {
        if (fromText.isNotBlank() && toText.isNotBlank()) {
            try {
                val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                val start = format.parse(fromText)!!
                val end = format.parse(toText)!!

                val diff = end.time - start.time
                val minutes = (diff / (1000 * 60)).toInt()
                val hours = minutes / 60
                val remainingMinutes = minutes % 60
                durationText = "Trajanje: $hours ur $remainingMinutes min"
            } catch (e: Exception) {
                // If parsing fails, keep the previous value or handle the error as needed
            }
        }
    }

    // Observe changes to 'fromText' and 'toText' and update duration
    LaunchedEffect(fromText, toText) {
        updateDuration()
    }

    // Klic funkcije za obvladovanje časa
    manageTimer(
        isButtonActive = isButtonActive,
        startTime = startTime,
        elapsedMinutes = elapsedMinutes,
        fromText = fromText,
        toText = toText,
        durationText = durationText,
        onStartTimeChanged = { newFromText -> fromText = newFromText },
        onToTimeChanged = { newToText -> toText = newToText },
        onDurationTextChanged = { newDurationText -> durationText = newDurationText },
        onElapsedMinutesChanged = { newElapsedMinutes -> elapsedMinutes = newElapsedMinutes }
    )

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

        // Text input for report description
        ReportTextField()

        // Recording buttons
        RecordingControls()


        if (isButtonActive) {
            Text(
                text = "Čas se štopa",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            Text(
                text = "",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Vnosni čas za "od" čas, "do" čas, in trajanje
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // "Od ura" field
            TextField(
                value = fromText,
                onValueChange = {
                    fromText = it

                    // Preverimo, če je vnos v formatu HH:mm
                    if (fromText.isNotBlank() && fromText.matches(Regex("^\\d{2}:\\d{2}$"))) {
                        // Ko se "Od ura" ročno spremeni, se posodobi trajanje
                        if (fromText.isNotBlank() && toText.isNotBlank()) {
                            try {
                                val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                                val startDate = formatter.parse(fromText)
                                val endDate = formatter.parse(toText)
                                if (startDate != null && endDate != null) {
                                    val durationInMillis = endDate.time - startDate.time
                                    val durationInMinutes = durationInMillis / (1000 * 60)
                                    val hours = durationInMinutes / 60
                                    val minutes = durationInMinutes % 60
                                    durationText = "Trajanje: $hours ur $minutes min"
                                }
                            } catch (e: Exception) {
                                // V primeru napake pri analizi časa preprosto ignoriraj spremembe
                                println("Napaka pri analizi časa: ${e.message}")
                            }
                        }
                    }
                },
                label = { Text("Od ura") },
                modifier = Modifier.weight(0.6f),  // Nastavi širino
                enabled = !isButtonActive,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // "Do ura" field
            TextField(
                value = toText,
                onValueChange = { toText = it },
                label = { Text("Do ura") },
                modifier = Modifier.weight(0.6f), // Nastavi širino
                enabled = !isButtonActive,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // "Trajanje" field
            TextField(
                value = durationText,
                onValueChange = {},
                label = { Text("Trajanje") },
                modifier = Modifier.weight(1.4f),
                enabled = false
            )
        }
        // Start/Stop Timer Button
        Button(
            onClick = { startStopTimer() },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(if (isButtonActive) "Stop" else "Štopaj")
        }

        // User selection dropdown
        SimpleDropdownMenu(viewModel)

        // Distance input
        DistanceInput()

        // Submit report button
        SubmitButton(navController, distanceInKm = "")

        // Navigation buttons
        NavigationButtons(navController)
    }
}

// Funkcija za upravljanje časa
fun manageTimer(
    isButtonActive: Boolean,
    startTime: Date?,
    elapsedMinutes: Int,
    fromText: String,
    toText: String,
    durationText: String,
    onStartTimeChanged: (String) -> Unit,
    onToTimeChanged: (String) -> Unit,
    onDurationTextChanged: (String) -> Unit,
    onElapsedMinutesChanged: (Int) -> Unit
) {
    if (isButtonActive) {
        // Če timer teče, izračunaj trajanje vsako minuto
        if (startTime == null) {
            // Če ni začetnega časa, nastavi trenutni čas kot začetni čas
            val currentTime = Date()
            onStartTimeChanged(SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentTime))
        } else {
            // Vsako minuto posodobi trajanje
            val currentTime = Date()
            val elapsedTimeInMinutes = ((currentTime.time - startTime.time) / (1000 * 60)).toInt()
            val hours = elapsedTimeInMinutes / 60
            val minutes = elapsedTimeInMinutes % 60
            onElapsedMinutesChanged(elapsedTimeInMinutes)

            // Posodobi trajanje v besedilu
            onDurationTextChanged("Trajanje: $hours ur $minutes min")
        }
    } else {
        // Ko je timer ustavljen, nastavi "to" čas in trajanje
        if (startTime != null) {
            val currentTime = Date()
            if (toText.isBlank()) {
                onToTimeChanged(SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentTime))
            }

            // Posodobi trajanje
            val elapsedTimeInMinutes = ((currentTime.time - startTime.time) / (1000 * 60)).toInt()
            val hours = elapsedTimeInMinutes / 60
            val minutes = elapsedTimeInMinutes % 60
            onDurationTextChanged("Trajanje: $hours ur $minutes min")
        }
    }
}

@Composable
fun SubmitButton(navController: NavController, distanceInKm: String) {
    val context = LocalContext.current // Get context to show Toast
    var showToast by remember { mutableStateOf(false) } // To trigger the toast when needed

    // Display the Toast when `showToast` is true
    LaunchedEffect(showToast) {
        if (showToast) {
            Toast.makeText(context, "Izpolnite vsa polja", Toast.LENGTH_SHORT).show()
        }
    }

    Button(
        onClick = {
            if (distanceInKm.isNotBlank()) {
                // If distance is not empty, navigate to "Report" screen
                navController.navigate("Report")
            } else {
                // Show the toast if distance is empty
                showToast = true
            }
        }
    ) {
        Text("Oddaj poročilo") // Button label
    }
}


// Report Text Field
@Composable
fun ReportTextField() {
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
}

// Recording Controls
@Composable
fun RecordingControls() {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var audioFile: File? by remember { mutableStateOf(null) }
    val audioRecorder = remember { AudioRecorder(context) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                if (isRecording) { // Stop recording
                    isRecording = false
                    audioRecorder.stopRecording()
                    audioFile = audioRecorder.getAudioFile()
                    Toast.makeText(context, "Snemanje ustavljeno", Toast.LENGTH_SHORT).show()
                } else { // Start recording
                    isRecording = true
                    audioRecorder.startRecording()
                    Toast.makeText(context, "Snemanje začeto", Toast.LENGTH_SHORT).show()
                }
            },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.mikrofon),
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = if (isRecording) stringResource(R.string.btn_stop_recording) else stringResource(R.string.btn_start_recording),
                maxLines = 2,
                textAlign = TextAlign.Center
            )
        }

        if (audioFile != null) {
            Spacer(modifier = Modifier.width(8.dp))
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
                    audioFile?.let {
                        playAudioFile(it, context)
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
}

// Time Entry Controls
@Composable
fun TimeEntryControls(fromText: String, toText: String, isButtonActive: Boolean, elapsedMinutes: Int, startTime: Date?, onFromTextChange: (String) -> Unit, onToTextChange: (String) -> Unit) {
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
                    onFromTextChange(it)
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
                    onToTextChange(it)
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
}

// User Selection Dropdown
@Composable
fun SimpleDropdownMenu(viewModel: PorociloLovecViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<String?>(null) }

    // Collect the list of users from the ViewModel (StateFlow)
    val userList by viewModel.usersByIds.collectAsState()

    // Fetch users only once when the current user id changes
    LaunchedEffect(viewModel.getCurrentUserId()) {
        val workerIDs = viewModel.getManagerIdsForHunter(viewModel.getCurrentUserId())
        if (workerIDs.isNotEmpty()) {
            viewModel.getUsersByIds(workerIDs) // Fetch users asynchronously
        }
        Log.d("AAA", "${viewModel.getCurrentUserId()} -> Users fetched: ${userList.size}")
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = "Izberite upravljalca lovišča:", fontWeight = FontWeight.Bold)

        Box(modifier = Modifier.fillMaxWidth().clickable { expanded = true }) {
            Text(text = selectedUser ?: "Klikni za izbiro", modifier = Modifier.padding(8.dp))
        }

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray)
                    .verticalScroll(rememberScrollState())
            ) {
                if (userList.isEmpty()) {
                    Text("No users available.")
                } else {
                    userList.forEach { user ->
                        Text(
                            text = user.fullName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedUser = user.fullName
                                    expanded = false
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }


}

// Distance Input
@Composable
fun DistanceInput() {
    var distanceInKm by remember { mutableStateOf("") }
    OutlinedTextField(
        value = distanceInKm,
        onValueChange = {
            if (it.length <= 3) {
                distanceInKm = it
            }
        },
        label = { Text("Razdalja (km)") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number
        )
    )
}



// Navigation Buttons
@Composable
fun NavigationButtons(navController: NavController) {
    Button(
        onClick = { navController.navigate("History") },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Poglej zgodovino poročil")
    }

    Button(
        onClick = { navController.navigate("Home") },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Nazaj na začetni zaslon")
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
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
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