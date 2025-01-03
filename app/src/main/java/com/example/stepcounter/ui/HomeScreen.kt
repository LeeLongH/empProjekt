package com.example.stepcounter.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stepcounter.R
import com.example.stepcounter.StepCounterScreen

@Composable
fun HomeScreen(viewModel: StepCounterViewModel = viewModel(),
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
        Button(onClick = { navController.navigate(StepCounterScreen.Search.name)}) {
            Text(text = "Poišči Čuvaje ali Upravljalce Lovišč")        }

        Spacer(modifier = Modifier.height(16.dp))

        // Napiši poročilo button
        Button(onClick = { navController.navigate(StepCounterScreen.Report.name) }) {
            Text(text = "Napiši poročilo")        }

        Spacer(modifier = Modifier.height(16.dp))

        // Zgodovina poročil button
        Button(onClick = { navController.navigate(StepCounterScreen.History.name) }) {
            Text(text = "Pregled poročil")        }
    }
}

