package com.example.porocilolovec.ui


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.animation.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberUpdatedState
import okhttp3.RequestBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.json.JSONException
import org.json.JSONObject


/*
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
    var durationText by remember { mutableStateOf("0 ur 0 min") }
    var distanceInKm by remember { mutableStateOf("") }
    var reportText by remember { mutableStateOf("") }
    val transcribedText = remember { mutableStateOf("") }

    RequestPermissions(context)

    // Start/Stop Timer
    fun startStopTimer() {
        if (!isButtonActive) {
            isButtonActive = true
            if (fromText.isBlank()) {
                startTime = Date()
                fromText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTime!!)
            }
            durationText = "0 ur 0 min"
        } else {
            isButtonActive = false
            val endTime = Date()
            elapsedMinutes = ((endTime.time - startTime!!.time) / (1000 * 60)).toInt()
            val hours = elapsedMinutes / 60
            val minutes = elapsedMinutes % 60
            toText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(endTime)

            updateDuration(fromText, toText, isButtonActive) { newDurationText ->
                durationText = newDurationText
            }
        }
    }

    LaunchedEffect(isButtonActive, toText, fromText) {
        while (isButtonActive || toText.isBlank()) {
            updateDuration(fromText, toText, isButtonActive) { newDurationText ->
                durationText = newDurationText
            }
            delay(60000) // Update every minute
        }
    }

    // Observe changes to 'fromText' and 'toText' to trigger updates
    LaunchedEffect(fromText, toText) {
        updateDuration(fromText, toText, isButtonActive) { newDurationText ->
            durationText = newDurationText
            Log.e("CCC", "LaunchedEffect updated durationText: $durationText")
        }
    }

    // Timer management function
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
        ReportTextField(
            reportText = reportText,
            onReportTextChange = { reportText = it }, // Updates `reportText`
            transcribedText = transcribedText // Keeps `transcribedText` reactive
        )

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
                    updateDuration(fromText, toText, isButtonActive) { newDurationText ->
                        durationText = newDurationText  // ⬅️ UI bo zdaj osvežen!
                    }
                },
                label = { Text("Od ura") },
                modifier = Modifier.weight(0.6f),
                enabled = !isButtonActive,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // "Do ura" field
            TextField(
                value = toText,
                onValueChange = {
                    toText = it
                    updateDuration(fromText, toText, isButtonActive) { newDurationText ->
                        durationText = newDurationText  // ⬅️ UI bo zdaj osvežen!
                    }
                },
                label = { Text("Do ura") },
                modifier = Modifier.weight(0.6f),
                enabled = !isButtonActive,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Prikaz trajanja
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


        DistanceInput(distanceInKm, onDistanceChange = { distanceInKm = it }) // Pass state and updater

        var selectedManagerID by remember { mutableStateOf<Int?>(null) } // 🔥 Shranjujemo ID
        DropdownManagers(viewModel) { selectedManagerID = it }

        Button(
            onClick = {
                if (reportText.isNotBlank() && selectedManagerID != null) {
                    val distance = distanceInKm.toFloatOrNull() ?: 0f

                    viewModel.submitReport(
                        selectedManagerID = selectedManagerID!!, // 🔥 Uporabimo ID namesto hardcoded 1
                        text = reportText,
                        distance = distance,
                        timeOnTerrain = elapsedMinutes
                    )

                    navController.navigate("History")
                    Toast.makeText(context, "Poročilo uspešno oddano", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Nekatera polja so neizpolnjena", Toast.LENGTH_SHORT).show()
                    Log.d("AAA", "Manjkajoči podatki: $distanceInKm, $reportText, selectedManagerID=$selectedManagerID")
                }
            }
        ) {
            Text("Oddaj poročilo")
        }


        NavigationButtons(navController)

        ScrollIndicator()
    }
}

fun updateDuration(fromText: String, toText: String, isButtonActive: Boolean, onUpdate: (String) -> Unit) {
    if (fromText.isNotBlank()) {
        try {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            val start = format.parse(fromText)!!
            val end = when {
                toText.isNotBlank() -> format.parse(toText)!!
                isButtonActive -> Date() // Če je štoparica aktivna, uporabi trenutni čas
                else -> return // Ne računaj trajanja, če ni ciljne ure in gumb ni aktiven
            }

            val diff = end.time - start.time
            val minutes = (diff / (1000 * 60)).toInt()
            val hours = minutes / 60
            val remainingMinutes = minutes % 60

            val newDuration = "$hours ur $remainingMinutes min"
            Log.e("CCC", "Updated duration: $newDuration")
            onUpdate(newDuration) // Tu se durationText spremeni!
        } catch (e: Exception) {
            Log.e("CCC", "Napaka pri analizi časa: ${e.message}")
        }
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
            onDurationTextChanged("$hours ur $minutes min")
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
            onDurationTextChanged("$hours ur $minutes min")
        }
    }
}


@Composable
fun ReportTextField(
    reportText: String,
    onReportTextChange: (String) -> Unit,
    transcribedText: MutableState<String>
) {
    val updatedText by rememberUpdatedState(transcribedText.value) // 🔥 Always follow the latest transcribed text

    LaunchedEffect(updatedText) {
        onReportTextChange(updatedText) // 🔥 Automatically update when transcribed text changes
    }

    OutlinedTextField(
        value = reportText,
        onValueChange = { onReportTextChange(it) }, // 🔥 Pass updates to parent
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



@Composable
fun DropdownManagers(viewModel: PorociloLovecViewModel, onUserSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<String?>(null) }
    var selectedUserId by remember { mutableStateOf<Int?>(null) } // 🔥 Dodamo ID

    val userList by viewModel.usersByIds.collectAsState()

    LaunchedEffect(viewModel.getCurrentUserId()) {
        val managerIDs = viewModel.getManagerIdsForHunter()
        if (managerIDs.isNotEmpty()) {
            viewModel.getUsersByIds(managerIDs)
        }
        Log.d("AAA", "Users fetched: $managerIDs")
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
                                    selectedUserId = user.userID // 🔥 Shrani tudi ID
                                    expanded = false
                                    onUserSelected(user.userID) // 🔥 Posreduj ID v glavno funkcijo
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun DistanceInput(distanceInKm: String, onDistanceChange: (String) -> Unit) {
    OutlinedTextField(
        value = distanceInKm,
        onValueChange = {
            if (it.length <= 3) {
                onDistanceChange(it) // 🔥 Call the parent’s state update function
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

        val simulatedResponse = """{"text":"zamenjaj s pravim API klicem, saj dela."}"""

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

*/
