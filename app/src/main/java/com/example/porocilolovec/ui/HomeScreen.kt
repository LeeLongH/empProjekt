package com.example.porocilolovec.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.porocilolovec.R
import com.example.porocilolovec.Hierarchy

@Composable
fun HomeScreen(viewModel: PorociloLovecViewModel = viewModel(),
               navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Poišči cuvaje button
        Button(onClick = { navController.navigate(Hierarchy.Search.name)}) {
            Text(text = stringResource(R.string.title_find_users))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Napiši poročilo button
        Button(onClick = { navController.navigate(Hierarchy.Report.name) }) {
            Text(text = stringResource(R.string.text_write_report))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Zgodovina poročil button
        Button(onClick = { navController.navigate(Hierarchy.History.name) }) {
            Text(text = stringResource(R.string.title_history_report))
        }
        Spacer(modifier = Modifier.height(32.dp))
        // LoginRegister
        Button(onClick = { navController.navigate(Hierarchy.LoginRegister.name) }) {
            Text(text = stringResource(R.string.btn_login_register))
        }
    }
}

