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

    val uiState = viewModel.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "${uiState.stepCount} / ${uiState.stepGoal} Steps",
            fontSize = 28.sp,
            color = Color.Blue,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = uiState.stepCount.toFloat() / uiState.stepGoal,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color.Blue
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Navigate to GoalScreen
        Button(onClick = {navController.navigate(StepCounterScreen.Goal.name)}) {
            Text(text = stringResource(R.string.daily_goal))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Navigate to HistoryScreen
        Button(onClick = {navController.navigate(StepCounterScreen.History.name)}) {
            Text(text = stringResource(R.string.history))
        }
    }
}

