package com.example.porocilolovec.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.porocilolovec.data.UserDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class PorociloLovecViewModel(private val userDAO: UserDAO) : ViewModel() {

    // Holds the app's UI state
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    // Function to register a new user
    fun registerUser(uniqueID: Int, name: String, surname: String, profession: String, email: String) {
        val user = User(uniqueID, name, surname, profession, email)
        viewModelScope.launch {
            userDAO.insert(user) // Shranjevanje uporabnika v bazo
            _userState.value = user //shranjevanje v ui state
        }
    }

    // Function to get users by profession
    fun getUsersByProfession(profession: String): Flow<List<User>> {
        return flow {
            emit(userDAO.getUsersByProfession(profession)) // Pridobi uporabnike iz baze
        }
    }

    // Function to load the current user (if any exists)
    fun loadUserById(userId: Int) {
        viewModelScope.launch {
            val user = userDAO.getUserById(userId.toString())
            _userState.value = user //shranjevanje v ui state
        }
    }


    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("EMAIL", null)
        val password = sharedPreferences.getString("PASSWORD", null)
        return !email.isNullOrEmpty() && !password.isNullOrEmpty()
    }
}