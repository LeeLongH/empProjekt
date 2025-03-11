package com.example.porocilolovec.ui


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import androidx.compose.animation.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberUpdatedState
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.json.JSONException
import org.json.JSONObject

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

    val transcribedText = remember { mutableStateOf("") }

    RequestPermissions(context)

    fun updateDuration() {
        if (fromText.isNotBlank()) {
            try {
                val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                val start = format.parse(fromText)!!

                val end = if (toText.isNotBlank()) {
                    format.parse(toText)!!
                } else if (isButtonActive) {
                    Date() // If button is active, update dynamically every minute
                } else {
                    return // If `toText` is blank and button is inactive, don't update
                }

                val diff = end.time - start.time
                val minutes = (diff / (1000 * 60)).toInt()
                val hours = minutes / 60
                val remainingMinutes = minutes % 60

                durationText = "Trajanje: $hours ur $remainingMinutes min"
            } catch (e: Exception) {
                // Handle parsing error
            }
        }
    }

    // Start/Stop Timer
    fun startStopTimer() {
        if (!isButtonActive) {
            isButtonActive = true
            if (fromText.isBlank()) {
                startTime = Date()
                fromText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTime!!)
            }
            durationText = "Trajanje: 0 ur 0 min"
        } else {
            isButtonActive = false
            val endTime = Date()
            elapsedMinutes = ((endTime.time - startTime!!.time) / (1000 * 60)).toInt()
            val hours = elapsedMinutes / 60
            val minutes = elapsedMinutes % 60
            toText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(endTime)

            updateDuration()
        }
    }

    // Update every minute while the timer is active OR if `toText` is blank
    LaunchedEffect(isButtonActive, toText) {
        while (isButtonActive || toText.isBlank()) {
            updateDuration()
            delay(60000) // Update every minute
        }
    }

    // Observe changes to 'fromText' and 'toText' to trigger updates
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


    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .drawVerticalScrollbar(scrollState)  // Indikator na desni strani
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
        ReportTextField(transcribedText)

        // Recording buttons
        RecordingControls(transcribedText)


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

        ScrollIndicator()
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

@Composable
fun ReportTextField(transcribedText: MutableState<String>) {
    var reportText by remember { mutableStateOf("") }
    val updatedText by rememberUpdatedState(transcribedText.value) // Vedno sledi zadnji vrednosti

    LaunchedEffect(updatedText) {
        reportText = updatedText
    }

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
fun RecordingControls(transcribedText: MutableState<String>) {
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
                    convertAudioToTextTest(audioFile!!, context, transcribedText)
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
                    Text("Nimate dodanih upravljalcev lovišč")
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


fun convertAudioToText(audioFile: File, context: Context, transcribedText: MutableState<String>) {
    if (!audioFile.exists() || audioFile.length() == 0L) {
        Log.e("SSS", "Napaka: Zvočna datoteka ne obstaja ali je prazna.")
        return
    }

    val client = OkHttpClient()
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", audioFile.name, RequestBody.create("audio/wav".toMediaTypeOrNull(), audioFile))
        .addFormDataPart("model", "whisper-1") // Model v body
        .build()

    val request = Request.Builder()
        .url("https://api.openai.com/v1/audio/transcriptions") // OpenAI Whisper API
        .post(requestBody)
        .addHeader("Authorization", "")
        .addHeader("Accept", "application/json")
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: "No response received"

            if (response.isSuccessful) {
                Log.d("SSS", "Uspeh: $responseBody")
                // Extracting the transcription from the response JSON
                val jsonResponse = JSONObject(responseBody)
                val transcription = jsonResponse.optString("text", "No transcription found")

                // Updating the transcribedText state on the main thread
                (context as Activity).runOnUiThread {
                    transcribedText.value = transcription // Update the TextField with the transcription
                    Toast.makeText(context, "Transkripcija: $transcription", Toast.LENGTH_LONG).show()
                }
            } else {
                Log.e("SSS", "Napaka: $responseBody")
                (context as Activity).runOnUiThread {
                    Toast.makeText(context, "Napaka: $responseBody", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: IOException) {
            Log.e("SSS", "Zahteva ni uspela: ${e.message}")
            (context as Activity).runOnUiThread {
                Toast.makeText(context, "Zahteva ni uspela: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}



fun convertAudioToTextTest(
    audioFile: File,
    context: Context,
    transcribedText: MutableState<String>
) {
    if (!audioFile.exists() || audioFile.length() == 0L) {
        Log.e("SSS", "Napaka: Zvočna datoteka ne obstaja ali je prazna.")
        return
    }

    CoroutineScope(Dispatchers.IO).launch {
        delay(1000) // Simulacija mrežnega zakasnitve

        val simulatedResponse = """{"text":"Testiranje dve pa ta je poročila."}"""

        // Posodobi UI na glavnem niti
        (context as Activity).runOnUiThread {
            try {
                val jsonResponse = JSONObject(simulatedResponse)
                val transcription = jsonResponse.optString("text", "No transcription found")
                transcribedText.value = transcription
                Toast.makeText(context, "Transkripcija: $transcription", Toast.LENGTH_LONG).show()
            } catch (e: JSONException) {
                Log.e("SSS", "Napaka pri obdelavi JSON: ${e.message}")
                Toast.makeText(context, "Napaka pri obdelavi JSON", Toast.LENGTH_LONG).show()
            }
        }
    }
}





@Composable
fun Modifier.drawVerticalScrollbar(scrollState: ScrollState): Modifier {
    return this.drawWithContent {
        drawContent()
        val showScrollbar = scrollState.maxValue > 0  // Pokaži le, če je vsebina večja od zaslona
        if (showScrollbar) {
            val scrollbarHeight = size.height * (size.height / (size.height + scrollState.maxValue))
            val scrollbarY = size.height * (scrollState.value.toFloat() / scrollState.maxValue)
            drawRect(
                color = Color.Gray.copy(alpha = 0.5f),
                topLeft = Offset(size.width - 8.dp.toPx(), scrollbarY),
                size = Size(4.dp.toPx(), scrollbarHeight)
            )
        }
    }
}

@Composable
fun ScrollIndicator() {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            isVisible = !isVisible
            delay(1000)
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Scroll down",
            tint = Color.Gray,
            modifier = Modifier.size(32.dp)
        )
    }
}
