package com.example.porocilolovec.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.porocilolovec.data.OfflineRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest


class PorociloLovecViewModel(private val offlineRepo: OfflineRepo, private val context: Context) : ViewModel() {



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

    // Use a regular String to store the work requests (no need for Flow or LiveData)
    var workRequests: String = ""
        private set

    // Retrieve work requests for the current user
    fun getWorkRequests() {
        val currentUserId = getCurrentUserId()
        if (currentUserId == -1) {
            Log.e("WORK_REQUEST", "User is not logged in.")
            return
        }

        viewModelScope.launch {
            try {
                // Call OfflineRepo to get work requests from the database
                val workRequestsString = offlineRepo.getWorkRequests(currentUserId)
                workRequests = workRequestsString ?: "" // Set the workRequests to the fetched value
                Log.d("WORK_REQUEST", "Work requests retrieved: $workRequestsString")
            } catch (e: Exception) {
                Log.e("WORK_REQUEST", "Error retrieving work requests: ${e.message}")
            }
        }
    }


    fun rejectWorkRequest(requestToRemove: String) {
        val currentUserId = getCurrentUserId()
        viewModelScope.launch {
            offlineRepo.rejectWorkRequest(currentUserId, requestToRemove)
        }
    }

    // Update users list after rejection
    fun updateUsersList(updatedUsers: List<User>) {
        _usersByIds.value = updatedUsers
    }

    suspend fun acceptWorkRequest(targetUserId: Int) {
        val currentUserId = getCurrentUserId()
        offlineRepo.acceptWorkRequest(currentUserId, targetUserId)

        // Remove the accepted user from the list
        val updatedUsers = _usersByIds.value.filterNot { it.userID == targetUserId }
        _usersByIds.value = updatedUsers

        rejectWorkRequest(targetUserId.toString())
    }
    suspend fun getManagerIdsForHunter(workerID: Int): List<Int> {
        return offlineRepo.getManagerIdsForHunter(workerID)
    }


    private val _usersByIds = MutableStateFlow<List<User>>(emptyList())
    val usersByIds: StateFlow<List<User>> = _usersByIds

    // Function to fetch users by their IDs
    fun getUsersByIds(userIds: List<Int>) {
        viewModelScope.launch {
            val result = offlineRepo.getUsersByIds(userIds)
            _usersByIds.value = result // This is the correct way to update the value
            Log.d("AAA", "Fetched users by IDs: ${result.size}")
        }
    }

    fun getConnection(managerId: Int, workerId: Int): Int? {
        var connectionID: Int? = null
        viewModelScope.launch {
            connectionID = offlineRepo.getConnection(managerId, workerId)
        }
        return connectionID
    }













    private val _reportSaved = MutableStateFlow(false) // ✅ StateFlow instead of LiveData
    val reportSaved: StateFlow<Boolean> = _reportSaved

    fun submitReport(
        selectedManagerID: Int, // 🔥 No need to pass full Connections object
        text: String,
        distance: Float,
        timeOnTerrain: Int
    ) {
        val userID = getCurrentUserId() // Get the current user's ID

        viewModelScope.launch {
            try {
                // Get connection ID from DAO
                val connectionID = offlineRepo.getConnection(selectedManagerID, userID)

                // Call repository to insert the report
                offlineRepo.submitReport(userID, connectionID, text, distance, timeOnTerrain)

                _reportSaved.value = true // Report saved successfully
            } catch (e: Exception) {
                _reportSaved.value = false
                Log.e("submitReport", "Error saving report", e) // Log the error
            }
        }
    }




    fun getReports(): Flow<List<Reports>> {
        return offlineRepo.getReportsForUser(getCurrentUserId()) // 🔥 Preprosto posreduj naprej
    }

    private val _reports = MutableStateFlow<List<Reports>>(emptyList()) // Mutable state flow to hold reports
    val reports: StateFlow<List<Reports>> = _reports // Expose as StateFlow

    // Call repository to fetch reports and emit them
    fun fetchReportsForUser() {
        viewModelScope.launch {
            offlineRepo.getReportsForUser(getCurrentUserId()).collect { reportList ->
                // Log the fetched reports
                Log.d("PorociloLovecViewModel", "Fetched reports: ${reportList.size}")
                _reports.value = reportList
            }
        }
    }

    // Make sure to call this function in onCreate/onStart of your UI
    init {
        fetchReportsForUser()
    }
















}