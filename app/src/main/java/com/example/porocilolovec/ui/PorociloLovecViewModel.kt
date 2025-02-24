package com.example.porocilolovec.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.porocilolovec.data.OfflineRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PorociloLovecViewModel(private val offlineRepo: OfflineRepo, private val context: Context) : ViewModel() {

    private val _reports = MutableStateFlow<List<Reports>>(emptyList())
    val reports: StateFlow<List<Reports>> = _reports

    private val _usersByProfession = MutableStateFlow<List<User>>(emptyList())
    val usersByProfession: StateFlow<List<User>> = _usersByProfession

    private val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    // Function to register user
    fun registerUser(user: User) {
        viewModelScope.launch {
            offlineRepo.registerUser(user.fullName, user.email, user.password, user.profession)
        }
    }

    // Function to login user
    fun loginUser(email: String, password: String, context: Context, onSuccess: (User?) -> Unit) {
        viewModelScope.launch {
            val user = offlineRepo.loginUser(email, password)

            if (user != null) {
                saveUserData(context, user)
                onSuccess(user)
            } else {
                onSuccess(null)
                Toast.makeText(context, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Save user data to SharedPreferences
    fun saveUserData(context: Context, user: User) {
        with(sharedPreferences.edit()) {
            putInt("USER_ID", user.userID)
            putString("USER_NAME", user.fullName)
            putString("USER_EMAIL", user.email)
            putString("USER_PASSWORD", user.password)
            putString("USER_PROFESSION", user.profession)
            apply()
        }
        Log.d("USER_PREFS", "User data saved: $user")
    }

    // Retrieve the current user ID from SharedPreferences
    fun getCurrentUserId(): Int {
        return sharedPreferences.getInt("USER_ID", -1)
    }
    // Function to retrieve the current user profession from SharedPreferences
    fun getCurrentUserProfession(): String {
        return sharedPreferences.getString("USER_PROFESSION", "No profession found") ?: "No profession found"
    }


    // Clear user data from SharedPreferences
    fun clearUserData(context: Context) {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
        Log.d("USER_PREFS", "User data cleared.")
    }

    fun searchUsersByProfession(profession: String) {
        viewModelScope.launch {
            val result = offlineRepo.searchUsersByProfession(profession)
            _usersByProfession.value = result
            Log.d("PorociloLovecViewModel", "Users found: ${result.size}")
        }
    }


    // Function to get all users
    fun getAllUsers(onResult: (List<User>) -> Unit) {
        viewModelScope.launch {
            val users = offlineRepo.getAllUsers()
            onResult(users)
        }
    }

    // Function to send a work request to a target user
    fun sendWorkRequest(targetUserId: Int) {
        val currentUserId = getCurrentUserId()

        if (currentUserId == -1) {
            Log.e("WORK_REQUEST", "User is not logged in. Work request not sent.")
            return
        }

        // Proceed with sending the work request
        viewModelScope.launch {
            try {
                offlineRepo.sendWorkRequest(targetUserId, currentUserId.toString())
                Log.d("WORK_REQUEST", "Work request sent from user $currentUserId to user $targetUserId.")
            } catch (e: Exception) {
                Log.e("WORK_REQUEST", "Error sending work request: ${e.message}")
            }
        }
    }

    // Register function for clearing the user table
    fun clearUserTable() {
        viewModelScope.launch {
            offlineRepo.clearUserTable()
        }
    }
}