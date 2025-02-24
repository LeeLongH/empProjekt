package com.example.porocilolovec.ui


import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.porocilolovec.R
import androidx.compose.ui.res.painterResource


@Composable
fun WorkRequestsScreen(
    viewModel: PorociloLovecViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current

    // Retrieve the work request string from ViewModel
    val workRequestsString = viewModel.getCurrentWorkRequests()
    Log.d("LEON", "workRequestsString: $workRequestsString")

    // Convert space-separated string into a list of user IDs
    val userIds = workRequestsString.split(" ").mapNotNull { it.toIntOrNull() }
    Log.d("LEON", "User IDs: $userIds")
    // If no valid work requests exist, show a message and allow navigation home
    if (userIds.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No pending work requests.",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("Home") }) {
                Text(text = "Go to Home")
            }
        }
        return
    }

    // State for showing the dialog
    val openDialog = remember { mutableStateOf(false) }
    val selectedUserId = remember { mutableStateOf<Int?>(null) }

    // Handle user card click
    fun onUserClick(userId: Int) {
        selectedUserId.value = userId
        openDialog.value = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(
            text = "Pending Work Requests:",
            modifier = Modifier.padding(vertical = 8.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                userIds.forEach { userId ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUserClick(userId) }
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "User ID: $userId",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate("Home") },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Go to Home")
        }
    }

    // Dialog to confirm work request acceptance
    if (openDialog.value && selectedUserId.value != null) {
        UserActionDialog(
            userId = selectedUserId.value!!,
            onDismiss = { openDialog.value = false },
            onAcceptRequest = { targetUserId ->
                viewModel.acceptWorkRequest(targetUserId)
                openDialog.value = false
                Toast.makeText(context, "Work request accepted from User ID $targetUserId", Toast.LENGTH_SHORT).show()
            },
            onRejectRequest = {
                openDialog.value = false
                Toast.makeText(context, "Work request rejected from User ID ${selectedUserId.value}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun UserActionDialog(
    userId: Int,
    onDismiss: () -> Unit,
    onAcceptRequest: (Int) -> Unit,
    onRejectRequest: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Work Request Options") },
        text = { Text(text = "Do you want to accept the work request from User ID $userId?") },
        confirmButton = {
            Button(onClick = { onAcceptRequest(userId) }) { // Accept request
                Text("Accept Request")
            }
        },
        dismissButton = {
            Button(onClick = onRejectRequest) {
                Text("Reject Request")
            }
        }
    )
}