package com.example.porocilolovec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.porocilolovec.data.RoomDB
import com.example.porocilolovec.ui.PorociloLovecViewModel
import com.example.porocilolovec.ui.PorociloLovecViewModelFactory
import com.example.porocilolovec.ui.theme.StepCounterTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pridobi instanco Room baze
        val database = RoomDB.getDatabase(applicationContext)
        val userDAO = database.UserDAO()

        // Ustvari ViewModelFactory
        val viewModelFactory = PorociloLovecViewModelFactory(userDAO)

        // Ustvari ViewModel
        val viewModel = ViewModelProvider(this, viewModelFactory)[PorociloLovecViewModel::class.java]

        setContent {
            StepCounterTheme {
                StepCounterApp(viewModel) // Posreduj ViewModel v Compose
            }
        }
    }
}