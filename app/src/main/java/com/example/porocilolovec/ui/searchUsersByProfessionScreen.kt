package com.example.porocilolovec.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.porocilolovec.R

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext


@Composable
fun SearchUsersByProfessionScreen(
    viewModel: PorociloLovecViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getCurrentUserProfession(context) { profession ->
            viewModel.searchUsersByProfession(profession)
        }
    }

    // Observe users fetched by ViewModel
    val users = viewModel.usersByProfession.collectAsState().value

    // State for showing the dialog
    val openDialog = remember { mutableStateOf(false) }
    val selectedUser = remember { mutableStateOf<User?>(null) }

    // Function to handle user card click
    fun onUserClick(user: User) {
        selectedUser.value = user
        openDialog.value = true
    }

    // Handle the dialog result and navigate after action
    if (openDialog.value && selectedUser.value != null) {
        UserActionDialog(
            user = selectedUser.value!!,
            onDismiss = { openDialog.value = false },
            onSendRequest = { targetUserId ->
                viewModel.sendWorkRequest(context, targetUserId)
                openDialog.value = false
                Toast.makeText(context, "Work request sent to ${selectedUser.value?.fullName}", Toast.LENGTH_SHORT).show()
                // After sending the request, navigate to Home
                navController.navigate("Home") // This is where you should navigate after the action.
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(
            text = "Users Found:",
            modifier = Modifier.padding(vertical = 8.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopStart
        ) {
            if (users.isEmpty()) {
                Text(
                    text = "No users found.",
                    color = Color.Gray
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    users.forEach { user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUserClick(user) }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = when (user.profession) {
                                        "Upravljalec Lovišča" -> painterResource(id = R.drawable.upravljalec_lovisca)
                                        else -> painterResource(id = R.drawable.cuvaj)
                                    },
                                    contentDescription = "User Icon",
                                    modifier = Modifier.padding(end = 8.dp),
                                    tint = Color.Unspecified
                                )
                                Column {
                                    Text(
                                        text = user.fullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(text = "Email: ${user.email}", fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserActionDialog(
    user: User,
    onDismiss: () -> Unit,
    onSendRequest: (String) -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Work Request Options") },
        text = { Text(text = "Do you want to send a work request to ${user.fullName}?") },
        confirmButton = {
            Button(onClick = { onSendRequest(user.userID) }) {
                Text("Send Request")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


