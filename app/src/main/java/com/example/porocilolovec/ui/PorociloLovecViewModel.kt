package com.example.porocilolovec.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class PorociloLovecViewModel : ViewModel() {
    val database = FirebaseDatabase.getInstance("https://porocilolovec253-default-rtdb.europe-west1.firebasedatabase.app/")

    private val reportsRef = database.getReference("reports")
    private val connectionsRef = database.getReference("connections")
    private val counterRef = database.getReference("counter")

    private val db = FirebaseFirestore.getInstance()


    // REGISTER
    fun registerUser(user: User) {
        val usersCollection = db.collection("users") // Firestore auto-creates the collection

        usersCollection.add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("PorociloLovecViewModel", "✅ User added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("PorociloLovecViewModel", "❌ Error adding user: ${e.message}", e)
            }
    }

    fun saveUserData(context: Context, user: User) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("USER_ID", user.userID)
            putString("USER_NAME", user.fullName)
            putString("USER_EMAIL", user.email)
            putString("USER_PASSWORD", user.password)
            putString("USER_PROFESSION", user.profession)
            apply()
        }
    }

    fun clearUserData(context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    // LOGIN
    fun loginUser(email: String, password: String, context: Context, callback: (User?) -> Unit) {
        val usersCollection = db.collection("users")

        usersCollection
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val user = document.toObject(User::class.java)
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("PorociloLovecViewModel", "❌ Login failed: ${e.message}", e)
                callback(null)
            }
    }

    //SEARCH
    private val _usersByProfession = MutableStateFlow<List<User>>(emptyList())
    val usersByProfession: StateFlow<List<User>> = _usersByProfession

    fun getCurrentUserProfession(context: Context, onResult: (String?) -> Unit) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val currentUserEmail = sharedPreferences.getString("USER_EMAIL", null)

        if (currentUserEmail == null) {
            Log.e("PorociloLovecViewModel", "SharedPreferences: USER_EMAIL is null")
            onResult(null)
            return
        }

        Log.e("PorociloLovecViewModel", "SharedPreferences: Found email: $currentUserEmail")

        db.collection("users")
            .whereEqualTo("email", currentUserEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val profession = documents.documents[0].getString("profession")
                    Log.e("PorociloLovecViewModel", "Found profession: $profession")
                    onResult(profession)
                } else {
                    Log.e("PorociloLovecViewModel", "No user found with email: $currentUserEmail")
                    onResult(null)
                }
            }
            .addOnFailureListener {
                Log.e("PorociloLovecViewModel", "Error fetching user: ${it.message}")
                onResult(null)
            }
    }

    fun searchUsersByProfession(profession: String?) {
        Log.e("PorociloLovecViewModel", "Searching users by profession: $profession")

        if (profession.isNullOrBlank()) {
            _usersByProfession.value = emptyList()
            return
        }

        val professionsToSearch = when (profession.lowercase()) {
            "lovec" -> listOf("gospodar", "staresina")  // When profession is "Lovec", search for "Gospodar" and "Staresina"
            "gospodar" -> listOf("lovec")               // When profession is "Gospodar", search for "Lovec"
            "staresina" -> listOf("lovec")              // When profession is "Staresina", search for "Lovec"
            else -> listOf(profession)                  // Otherwise, search for the provided profession
        }

        db.collection("users")
            .whereIn("profession", professionsToSearch)
            .get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { it.toObject(User::class.java) }
                Log.e("PorociloLovecViewModel", "Found ${users.size} users matching profession(s) $professionsToSearch")
                _usersByProfession.value = users
            }
            .addOnFailureListener { e ->
                Log.e("PorociloLovecViewModel", "❌ Failed to fetch users: ${e.message}")
                _usersByProfession.value = emptyList()
            }
    }



}