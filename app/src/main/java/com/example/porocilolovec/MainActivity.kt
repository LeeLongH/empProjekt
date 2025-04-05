package com.example.porocilolovec

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.porocilolovec.data.UserDao
import com.example.porocilolovec.data.ConnectionsDao
import com.example.porocilolovec.ui.PorociloLovecViewModel
import com.example.porocilolovec.ui.PorociloLovecViewModelFactory
import com.example.porocilolovec.ui.theme.MainAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory = PorociloLovecViewModelFactory()
        val viewModel = ViewModelProvider(this, viewModelFactory)[PorociloLovecViewModel::class.java]


        setContent {
            MainAppTheme {
                PorociloLovecApp(viewModel) // ✅ Pass ViewModel to the UI
            }
        }

        // ✅ Retrieve user data from SharedPreferences (if needed)
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val savedId = sharedPreferences.getString("USER_ID", "No ID found")
        val savedName = sharedPreferences.getString("USER_NAME", "No name found")
        val savedEmail = sharedPreferences.getString("USER_EMAIL", "No email found")
        val savedProfession = sharedPreferences.getString("USER_PROFESSION", "No profession found")

        Log.d("LEON", "User ID: $savedId")
        Log.d("LEON", "Name: $savedName")
        Log.d("LEON", "Email: $savedEmail")
        Log.d("LEON", "Profession: $savedProfession")
    }
}