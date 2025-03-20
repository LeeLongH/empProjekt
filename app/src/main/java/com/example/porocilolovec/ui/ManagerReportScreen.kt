package com.example.porocilolovec.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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


@Composable
fun ManagerReportScreen(viewModel: PorociloLovecViewModel = viewModel(), navController: NavController) {


    DropdownMenuWorkers(viewModel)
}

@Composable
fun DropdownMenuWorkers(viewModel: PorociloLovecViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<String?>(null) }

    // Collect the list of users from the ViewModel (StateFlow)
    val userList by viewModel.usersByIds.collectAsState()

    // Fetch users only once when the current user id changes
    LaunchedEffect(viewModel.getCurrentUserId()) {
        val workerIDs = viewModel.getHunterIdsForManager()
        if (workerIDs.isNotEmpty()) {
            viewModel.getUsersByIds(workerIDs) // Fetch users asynchronously
        }
        Log.d("AAA", "${viewModel.getCurrentUserId()} -> Users fetched: ${userList.size}")
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = "Izberite lovca za ogled porocil:", fontWeight = FontWeight.Bold)

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
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

