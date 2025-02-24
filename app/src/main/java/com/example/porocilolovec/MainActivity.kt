package com.example.porocilolovec

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.porocilolovec.data.OfflineRepo
import com.example.porocilolovec.data.RoomDB
import com.example.porocilolovec.data.UserDao
import com.example.porocilolovec.ui.PorociloLovecViewModel
import com.example.porocilolovec.ui.PorociloLovecViewModelFactory
import com.example.porocilolovec.ui.theme.MainAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get Room Database instance
        val database = RoomDB.getDatabase(applicationContext)
        val userDao = database.userDao() // Getting the UserDao
        val reportDao = database.reportDao() // Getting the ReportDao

        // Initialize OfflineRepo with userDao and reportDao
        val offlineRepo = OfflineRepo(userDao, reportDao)

        // Pass context to ViewModelFactory
        val viewModelFactory = PorociloLovecViewModelFactory(offlineRepo, applicationContext)

        // Create ViewModel with the factory
        val viewModel = ViewModelProvider(this, viewModelFactory)[PorociloLovecViewModel::class.java]

        setContent {
            MainAppTheme {
                // Pass the ViewModel to the UI (Compose)
                PorociloLovecApp(viewModel)
            }
        }

        // ✅ Use applicationContext directly instead of LocalContext.current
        val sharedPreferences = applicationContext.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedId = sharedPreferences.getInt("USER_ID", -1)
        val savedName = sharedPreferences.getString("USER_NAME", "No name found")
        val savedEmail = sharedPreferences.getString("USER_EMAIL", "No email found")
        val savedPassword = sharedPreferences.getString("USER_PASSWORD", "No password found")
        val savedProfession = sharedPreferences.getString("USER_PROFESSION", "No profession found")

        Log.d("LEON", "oncreate User ID: $savedId")
        Log.d("LEON", "oncreate Name: $savedName")
        Log.d("LEON", "oncreate Email: $savedEmail")
        Log.d("LEON", "oncreate Password: $savedPassword")
        Log.d("LEON", "oncreate Profession: $savedProfession")
    }
}
