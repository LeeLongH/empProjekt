package com.example.porocilolovec.ui

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.porocilolovec.data.OfflineRepo
import com.example.porocilolovec.data.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch




class PorociloLovecViewModel(private val offlineRepo: OfflineRepo) : ViewModel() {

    // Holds the app's UI state for the user
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    private val _usersByProfession = MutableStateFlow<List<User>>(emptyList())
    val usersByProfession: StateFlow<List<User>> = _usersByProfession

    // Function to register a new user
    fun registerUser(uniqueID: Int, name: String, surname: String, profession: String, email: String, password: String) {
        val user = User(uniqueID, name, surname, email, reports = emptyMap(), profession, password)
        viewModelScope.launch {
            offlineRepo.addUser(user) // Save user to database
            _userState.value = user // Store the user in UI state
        }
    }

    // Function to get users by profession
    fun getUsersByProfession(profession: String) {
        viewModelScope.launch {
            try {
                val users = offlineRepo.getUsersByProfession(profession)
                _usersByProfession.value = users
            } catch (e: Exception) {
                _usersByProfession.value = emptyList() // Handle error by setting empty list
            }
        }
    }

    // Function to load a user by ID
    fun loadUserById(userId: Int) {
        viewModelScope.launch {
            val user = offlineRepo.getUserById(userId) // Retrieve user from the database
            _userState.value = user // Update the UI state with the user data
        }
    }

    // Function to check if the user is logged in
    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("EMAIL", null)
        val password = sharedPreferences.getString("PASSWORD", null)
        return !email.isNullOrEmpty() && !password.isNullOrEmpty()
    }

    // Function to add a friend
    fun addFriend(currentUserId: Int, friendUserId: Int) {
        viewModelScope.launch {
            try {
                offlineRepo.addFriend(currentUserId, friendUserId) // Add friend via repository
            } catch (e: Exception) {
                // Handle failure (e.g., show an error message)
            }
        }
    }

    // Function to add a report
    fun addReport(userId: Int, report: ReportEntity) {
        viewModelScope.launch {
            try {
                offlineRepo.addReport(userId, report) // Add report via repository
            } catch (e: Exception) {
                // Handle failure (e.g., show an error message)
            }
        }
    }

    fun loginUser(email: String, password: String, context: Context, navController: NavController) {
        viewModelScope.launch {
            val user = offlineRepo.loginUser(email, password) // Preveri tako email kot geslo
            if (user != null) {
                // Shranimo ID uporabnika v SharedPreferences
                val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putInt("LOGGED_IN_USER_ID", user.id).apply()

                // Lahko naložimo tudi celotnega uporabnika, če ga potrebujemo pozneje
                _userState.value = user // To bo omogočilo, da imaš dostop do uporabnika v ViewModelu

                // Premik na domačo stran
                navController.navigate("Home")
            } else {
                Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Add logout function to clear SharedPreferences
    fun logoutUser(context: Context) {
        // Clear session data in SharedPreferences
        offlineRepo.clearSession(context)

    }
}