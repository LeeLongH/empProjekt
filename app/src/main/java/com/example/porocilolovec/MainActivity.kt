package com.example.porocilolovec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.porocilolovec.data.OfflineRepo
import com.example.porocilolovec.data.RoomDB
import com.example.porocilolovec.data.UserDao
import com.example.porocilolovec.ui.PorociloLovecViewModel
import com.example.porocilolovec.ui.PorociloLovecViewModelFactory
import com.example.porocilolovec.ui.theme.StepCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get Room Database instance
        val database = RoomDB.getDatabase(applicationContext)
        val userDao = database.userDao() // Getting the UserDao
        val reportDao = database.reportDao() // Getting the ReportDao

        // Initialize OfflineRepo with userDao and reportDao
        val offlineRepo = OfflineRepo(userDao, reportDao)

        // Create ViewModelFactory and pass OfflineRepo to the ViewModel
        val viewModelFactory = PorociloLovecViewModelFactory(offlineRepo)

        // Create ViewModel
        val viewModel = ViewModelProvider(this, viewModelFactory)[PorociloLovecViewModel::class.java]

        setContent {
            StepCounterTheme {
                // Pass the ViewModel to the UI (Compose)
                StepCounterApp(viewModel)
            }
        }
    }
}