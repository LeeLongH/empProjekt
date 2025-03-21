package com.example.porocilolovec.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.porocilolovec.Hierarchy
import com.example.porocilolovec.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text


@Composable
fun ManagerReportScreen(viewModel: PorociloLovecViewModel = viewModel(), navController: NavController) {
    var selectedUserId by remember { mutableStateOf<Int?>(null) }
    val reports by viewModel.reports.collectAsState(initial = emptyList())


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        DropdownMenuWorkers(viewModel) { userId ->
            selectedUserId = userId
            viewModel.loadReportsForUser(userId) // 🚀 Pravilna funkcija
        }


        Spacer(modifier = Modifier.height(16.dp))
        Log.e("AAA", "Število poročil: ${reports.size}")
        if (selectedUserId != null) {
            LazyColumn {
                items(reports) { report ->
                    ReportsHunter(report, viewModel) // Dodano viewModel
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("Home") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nazaj na glavni zaslon")
        }
    }
}

@Composable
fun DropdownMenuWorkers(viewModel: PorociloLovecViewModel, onUserSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<String?>(null) }
    val userList by viewModel.usersByIds.collectAsState()

    LaunchedEffect(viewModel.getCurrentUserId()) {
        val workerIDs = viewModel.getHunterIdsForManager()
        if (workerIDs.isNotEmpty()) {
            viewModel.getUsersByIds(workerIDs)
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = "Izberite lovca za ogled poročil:", fontWeight = FontWeight.Bold)

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
                    Text("Nimate dodanih lovcev")
                } else {
                    userList.forEach { user ->
                        Text(
                            text = user.fullName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedUser = user.fullName
                                    expanded = false
                                    onUserSelected(user.userID) // Pridobi poročila
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
fun ReportsHunter(report: Reports, viewModel: PorociloLovecViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var newMessage by remember { mutableStateOf("") }
    var showInputField by remember { mutableStateOf(false) } // Kontrola prikaza vnosa
    val context = LocalContext.current

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
                        .heightIn(min = 50.dp, max = 200.dp)
                        .verticalScroll(rememberScrollState())
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
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
                    Text(if (showInputField) "Skrij vnos" else "Dodaj poročilu komentar")
                }

                // **Vnosno polje za komentar**
                if (showInputField) {
                    OutlinedTextField(
                        value = newMessage,
                        onValueChange = { newMessage = it }, // Zdaj bo delovalo
                        label = { Text("Vnesi komentar") },
                        modifier = Modifier.fillMaxWidth()
                    )


                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (newMessage.isNotBlank()) {
                                val newChatMessage = ChatMessage(
                                    sender = "Manager", // Lahko preveriš vlogo uporabnika
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

