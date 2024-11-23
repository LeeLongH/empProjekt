package com.example.stepcounter.ui

import StepCounterViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stepcounter.R
import com.example.stepcounter.data.stepHistory

data class StepHistory(val date: String, val steps: Int)

@Composable
fun HistoryScreen(viewModel: StepCounterViewModel = viewModel(), navController: NavController
) {

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(stepHistory) { stepHistory ->
            HistoryItem(stepHistory)
            Divider()
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.navigateUp() }) {
                Text(text = stringResource(R.string.back))
            }
        }

    }


}

@Composable
fun HistoryItem(stepHistory: StepHistory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = stepHistory.date, fontSize = 18.sp)
        Text(text = "${stepHistory.steps} steps", fontSize = 18.sp)
    }
}

