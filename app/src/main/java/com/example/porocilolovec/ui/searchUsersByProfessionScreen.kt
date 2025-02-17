package com.example.porocilolovec.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.porocilolovec.R

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun SearchUsersByProfessionScreen(
    viewModel: PorociloLovecViewModel = viewModel(),
    navController: NavController
) {
    val selectedProfession = remember { mutableStateOf("") }
    val users = viewModel.getUsersByProfession(selectedProfession.value).collectAsState(initial = emptyList())
    val showDialog = remember { mutableStateOf(false) }
    val selectedUser = remember { mutableStateOf<User?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.text_select_profession),
            modifier = Modifier.padding(bottom = 8.dp),
        )

        // Radio buttons for profession selection
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButtonWithLabel(
                label = stringResource(R.string.text_upravljalec_lovisca),
                isSelected = selectedProfession.value == "Upravljalec Lovišča",
                onClick = { selectedProfession.value = "Upravljalec Lovišča" }
            )
            RadioButtonWithLabel(
                label = stringResource(R.string.text_cuvaj),
                isSelected = selectedProfession.value == "Čuvaj",
                onClick = { selectedProfession.value = "Čuvaj" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Additional search logic can be added here */ },
            enabled = selectedProfession.value.isNotEmpty(),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(R.string.btn_search))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.text_found_users),
            modifier = Modifier.padding(vertical = 8.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopStart
        ) {
            if (users.value.isEmpty()) {
                Text(
                    text = stringResource(R.string.text_no_users_found),
                    color = Color.Gray
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    users.value.forEach { user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedUser.value = user
                                    showDialog.value = true
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = when (selectedProfession.value) {
                                        "Upravljalec Lovišča" -> painterResource(id = R.drawable.upravljalec_lovisca) // Uporabite svojo sliko iz drawable
                                        "Čuvaj" -> painterResource(id = R.drawable.cuvaj)
                                        else -> null // Če ni izbran noben poklic, ne prikaži ikone
                                    } as Painter,
                                    contentDescription = "User Icon",
                                    modifier = Modifier.padding(end = 8.dp),
                                    tint = Color.Unspecified // Lahko nastaviš barvo, če želiš
                                )
                                Column {
                                    Text(text = user.name + " " + user.surname, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "Email: ${user.email}", fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
        Button(
            onClick = {navController.navigate("Home")},
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(R.string.btn_home_screen))
        }

        // AlertDialog
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = {
                    Text(text = stringResource(R.string.dialog_title_confirmation))
                },
                text = {
                    Text(
                        text = stringResource(
                            R.string.dialog_text_confirmation,
                            selectedUser.value?.name ?: "",
                            selectedUser.value?.surname ?: ""
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                            // Potrditev: Tukaj lahko dodate svojo logiko za delo z izbranim uporabnikom
                        }
                    ) {
                        Text(text = stringResource(R.string.btn_confirm))
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text(text = stringResource(R.string.btn_cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        text = text,
        confirmButton = confirmButton,
        dismissButton = dismissButton
    )
}

@Composable
fun RadioButtonWithLabel(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}