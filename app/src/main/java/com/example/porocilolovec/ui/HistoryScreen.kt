package com.example.porocilolovec.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.porocilolovec.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text


@Composable
fun HistoryScreen(viewModel: PorociloLovecViewModel = viewModel(), navController: NavController) {
    // Zdaj uporabljamo ownReports namesto reports
    val reports = viewModel.ownReports.collectAsState(initial = emptyList()).value

    // Poskrbimo, da se podatki naložijo
    LaunchedEffect(Unit) {
        viewModel.loadOwnReports()
    }

    Log.d("HistoryScreen", "Reports list size: ${reports.size}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.title_history_report),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(reports) { report ->
                ReportItem(report, viewModel)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("Home") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.btn_home_screen))
        }
    }
}


@Composable
fun ReportItem(report: Reports, viewModel: PorociloLovecViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var newMessage by remember { mutableStateOf("") }
    var showInputField by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val formattedDate = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(report.timestamp))
            Text(text = "Datum: $formattedDate", fontWeight = FontWeight.Bold)

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Kilometri: ${report.distance}")
                Text(text = "Opis: ${report.text}")
                Text(text = "Čas na terenu: ${report.timeOnTerrain} min")

                Spacer(modifier = Modifier.height(16.dp))

                // **Prikaz pogovora**
                val messages = report.getResponseList()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    messages.forEach { message ->
                        Text(
                            text = "${message.sender}: ${message.message}",
                            fontSize = 14.sp,
                            fontWeight = if (message.sender == "Manager") FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // **Gumb za dodajanje komentarja**
                Button(
                    onClick = { showInputField = !showInputField },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (showInputField) "Skrij vnos" else "Dodaj komentar")
                }

                // **Vnosno polje za komentar**
                if (showInputField) {
                    OutlinedTextField(
                        value = newMessage,
                        onValueChange = { newMessage = it },
                        label = { Text("Vnesi komentar") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (newMessage.isNotBlank()) {
                                val newChatMessage = ChatMessage(
                                    sender = "Hunter", // Čuvar doda sporočilo
                                    message = newMessage,
                                    timestamp = System.currentTimeMillis()
                                )

                                viewModel.addResponseToReport(report, newChatMessage)
                                newMessage = "" // Po oddaji sporočila počisti vnos
                                showInputField = false // Skrij vnosno polje
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Pošlji")
                    }
                }
            }
        }
    }
}

