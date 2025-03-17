package com.example.porocilolovec.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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


@Composable
fun ManagerReportScreen(viewModel: PorociloLovecViewModel = viewModel(), navController: NavController) {
    // Collect the list of reports as State inside Composable
    val reports = viewModel.reports.collectAsState(initial = emptyList()).value

    // Log the reports to check if data is being received correctly
    Log.d("ManagerReportScreen", "Reports list size: ${reports.size}")
    reports.forEach {
        Log.d("ManagerReportScreen", "Report: ${it.reportID}, Timestamp: ${it.timestamp}")
    }

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

        // Display each report as a Card using LazyColumn
        LazyColumn {
            items(reports) { report ->
                ReportsHunter(report)
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
fun ReportsHunter(report: Reports) {
    // Track the expanded state to show/hide details
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded }, // Toggle the expanded state on click
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Format the timestamp into a readable date string
            val formattedDate = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(report.timestamp))
            Text(text = "Datum: $formattedDate", fontWeight = FontWeight.Bold)

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Kilometri: ${report.distance}")
                Text(text = "Opis: ${report.text}")
                Text(text = "Čas na terenu: ${report.timeOnTerrain} min")
            }
        }
    }
}


