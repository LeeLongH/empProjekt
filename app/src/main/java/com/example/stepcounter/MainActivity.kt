package com.example.stepcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.stepcounter.data.RoomDB
import com.example.stepcounter.ui.StepCounterViewModel
import com.example.stepcounter.ui.StepCounterViewModelFactory
import com.example.stepcounter.ui.theme.StepCounterTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pridobi instanco Room baze
        val database = RoomDB.getDatabase(applicationContext)
        val userDAO = database.UserDAO()

        // Ustvari ViewModelFactory
        val viewModelFactory = StepCounterViewModelFactory(userDAO)

        // Ustvari ViewModel
        val viewModel = ViewModelProvider(this, viewModelFactory)[StepCounterViewModel::class.java]

        setContent {
            StepCounterTheme {
                StepCounterApp(viewModel) // Posreduj ViewModel v Compose
            }
        }
    }
}