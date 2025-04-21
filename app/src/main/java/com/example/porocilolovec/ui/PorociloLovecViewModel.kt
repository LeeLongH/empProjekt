package com.example.porocilolovec.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.text.get

class PorociloLovecViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _usersByIds = MutableStateFlow<List<User>>(emptyList())
    val usersByIds: StateFlow<List<User>> get() = _usersByIds



    private val _currentUserProfession = MutableStateFlow("")
    val currentUserProfession: StateFlow<String> = _currentUserProfession

    fun registerUser(user: User, context: Context) {
        val counterRef = db.collection("counters").document("users")
        val usersCollection = db.collection("users")

        db.runTransaction { transaction ->
            // Attempt to get the snapshot for the counter document
            val snapshot = transaction.get(counterRef)

            // If the document doesn't exist, initialize it with nextId = 1
            if (!snapshot.exists()) {
                transaction.set(counterRef, mapOf("nextId" to 1L))
            }

            // Retrieve the current nextId value
            val nextId = snapshot.getLong("nextId") ?: 1L

            val userId = nextId.toString() // Firestore document ID should be a string
            user.userID = userId // Set this on your user model

            // Save the user with this generated ID
            val userRef = usersCollection.document(userId)
            transaction.set(userRef, user)

            // Update the counter to the next value
            transaction.update(counterRef, "nextId", nextId + 1)

        }.addOnSuccessListener {
            Log.d("PorociloLovecViewModel", "✅ User registered with ID: ${user.userID}")

            // Now that the user is registered, save the user data to SharedPreferences
            saveUserData(context, user)  // Save user data here after registration is successful
        }.addOnFailureListener { e ->
            Log.e("PorociloLovecViewModel", "❌ Failed to register user: ${e.message}", e)
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
            commit()  // Using commit() to ensure immediate persistence
        }
        Log.d("PorociloLovecViewModel", "preferences USER_ID: ${user.userID}, USER_PROFESSION: ${user.profession}")
    }

    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId

    private val _currentUserFullName = MutableStateFlow("")
    val currentUserFullName: StateFlow<String> = _currentUserFullName

    fun getCurrentUserId(context: Context) {
        val user = getUserFromPreferences(context)
        _currentUserId.value = user?.userID ?: ""
    }

    fun getCurrentUserFullName(context: Context) {
        val user = getUserFromPreferences(context)
        _currentUserFullName.value = user?.fullName ?: "Unknown"
    }



    fun clearUserData(context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    // LOGIN
    fun loginUser(email: String, password: String, callback: (User?) -> Unit) {
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

    fun searchUsersByProfession(profession: String?) {
        Log.e("PorociloLovecViewModel", "Searching users by profession: $profession")

        if (profession.isNullOrBlank()) {
            _usersByProfession.value = emptyList()
            return
        }

        val professionsToSearch = when (profession.lowercase()) {
            "lovec" -> listOf("gospodar", "staresina")
            "gospodar", "staresina" -> listOf("lovec")
            else -> listOf(profession.lowercase())
        }

        db.collection("users")
            .whereIn("profession", professionsToSearch)
            .get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { it.toObject(User::class.java) }
                Log.e("PorociloLovecViewModel", "Found ${users.size} users matching profession(s) $professionsToSearch")
                _usersByProfession.value = users
            }

    }

        // Fetch user profession from SharedPreferences or Firestore
    fun getCurrentUserProfession(context: Context, onResult: (String?) -> Unit) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val currentUserEmail = sharedPreferences.getString("USER_EMAIL", null)

        if (currentUserEmail == null) {
            _currentUserProfession.value = ""
            onResult(null)  // Return null if no email found
            return
        }

        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("email", currentUserEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val profession = documents.documents[0].getString("profession")
                    _currentUserProfession.value = profession.toString()
                    onResult(profession)  // Return the profession via the callback
                } else {
                    _currentUserProfession.value = ""
                    onResult(null)  // Return null if no user found
                }
            }
            .addOnFailureListener {
                _currentUserProfession.value = ""
                onResult(null)  // Return null in case of failure
            }
    }

    fun getUserProfession(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_PROFESSION", null)
    }

    fun getCurrentUser(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_ID", null)
    }

    fun getUserFromPreferences(context: Context): User? {
        val sharedPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userJson = sharedPrefs.getString("user", null)
        return if (userJson != null) {
            Gson().fromJson(userJson, User::class.java)
        } else null
    }


    fun getUsersByIds(userIds: List<String>) {
        if (userIds.isEmpty()) {
            _usersByIds.value = emptyList()
            return
        }

        // Reference to the Firestore users collection
        val usersCollection = db.collection("users")

        // Query for users where the document ID is in the list of userIds
        usersCollection.whereIn(FieldPath.documentId(), userIds)
            .get()
            .addOnSuccessListener { result ->
                val usersList = mutableListOf<User>()
                for (document in result) {
                    val user = document.toObject(User::class.java)
                    usersList.add(user)
                }
                _usersByIds.value = usersList
            }
            .addOnFailureListener { e ->
                Log.e("PorociloLovecViewModel", "❌ Error fetching users by IDs: ${e.message}")
            }
    }

    fun updateUsersList(updatedUsers: List<User>) {
        _usersByIds.value = updatedUsers
    }



    suspend fun getUserByEmailAndPassword(email: String, password: String): User? {
        return suspendCancellableCoroutine { continuation ->
            val usersCollection = db.collection("users")

            usersCollection
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.documents[0]
                        val user = document.toObject(User::class.java)
                        continuation.resume(user, null)
                    } else {
                        continuation.resume(null, null)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("PorociloLovecViewModel", "❌ Login failed: ${e.message}", e)
                    continuation.resume(null, null)
                }
        }
    }


    // ... Other functions ...

    fun addConnectionBetweenUsers(context: Context, targetUserId: String) {
        val connectionsCollection = db.collection("connections")

        val currentUserID = getCurrentUser(context) // implement this to retrieve the current user from SharedPreferences
        if (currentUserID == null) {
            Log.e("PorociloLovecViewModel", "Current user not found.")
            return
        }

        val connectionData = hashMapOf(
            "user1" to currentUserID,
            "user2" to targetUserId,
            "timestamp" to FieldValue.serverTimestamp()
        )

        connectionsCollection
            .add(connectionData)
            .addOnSuccessListener {
                Log.d("PorociloLovecViewModel", "✅ Connection added between ${currentUserID} and $targetUserId")
            }
            .addOnFailureListener { e ->
                Log.e("PorociloLovecViewModel", "❌ Failed to add connection: ${e.message}", e)
            }
    }


    fun submitReport(
        context: Context,
        selectedManagerID: String,
        text: String,
        distance: Float,
        timeOnTerrain: Int
    ) {
        val currentUserId = getCurrentUser(context)
        if (currentUserId == null) {
            Log.e("PorociloLovecViewModel", "⚠️ Cannot submit report: user ID not found in SharedPreferences.")
            return
        }

        val reportsCollection = db.collection("reports")

        val reportData = hashMapOf(
            "authorID" to currentUserId,
            "managerID" to selectedManagerID,
            "text" to text,
            "distance" to distance,
            "timeOnTerrain" to timeOnTerrain,
            "timestamp" to FieldValue.serverTimestamp()
        )

        reportsCollection
            .add(reportData)
            .addOnSuccessListener {
                Log.d("PorociloLovecViewModel", "✅ Report submitted successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("PorociloLovecViewModel", "❌ Failed to submit report: ${e.message}", e)
            }
    }

    suspend fun getConnectedManagerForHunter(hunterId: String): User? {
        val connectionSnapshot = db.collection("connections")
            .whereEqualTo("user1", hunterId)
            .get()
            .await()

        val managerId = connectionSnapshot.documents.firstOrNull()?.getString("user2")
        return if (managerId != null) {
            val userSnapshot = db.collection("users").document(managerId).get().await()
            userSnapshot.toObject(User::class.java)
        } else null
    }





    fun getUserIdFromPrefs(context: Context): String {
        val sharedPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("USER_ID", "") ?: ""
    }

    fun getUserFullNameFromPrefs(context: Context): String {
        val sharedPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("USER_NAME", "") ?: ""
    }

    fun getUserProfessionFromPrefs(context: Context): String {
        val sharedPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("USER_PROFESSION", "") ?: ""
    }

    fun getUserEmailFromPrefs(context: Context): String {
        val sharedPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("USER_EMAIL", "") ?: ""
    }


    private val _userToRegister = mutableStateOf<User?>(null)
    val userToRegister: State<User?> = _userToRegister

    // Set the user to register
    fun setUserToRegister(user: User?) {
        _userToRegister.value = user
    }

    // Register user after selection
    fun registerUserAfterSelection(user: User, context: Context) {
        // Save user data
        saveUserData(context, user)

        // Perform registration logic (e.g., API call or database insertion)
        Toast.makeText(context, "User Registered!", Toast.LENGTH_SHORT).show()
    }

    fun fetchOppositeUsers(context: Context) {
        val currentProfession = getUserProfessionFromPrefs(context)

        val targetProfession = when (currentProfession) {
            "Hunter" -> "Patron"
            "Patron", "Sage" -> "Hunter"
            else -> null
        }

        targetProfession?.let {
            searchUsersByProfession(it)
        }
    }



    private val _lovci = mutableStateOf<List<User>>(emptyList())
    val lovci: State<List<User>> = _lovci

    fun fetchLovci() {
        db.collection("users")
            .whereEqualTo("profession", "Lovec")
            .get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { it.toObject(User::class.java) }
                Log.d("🔥Firestore", "Fetched ${users.size} Lovci")
                _lovci.value = users
            }
            .addOnFailureListener { e ->
                Log.e("🔥Firestore", "Failed to fetch Lovci: ${e.message}")
                _lovci.value = emptyList()
            }
    }

    fun createConnection(userId: String, currentUser: User?) {
        // Check if currentUser is not null
        if (currentUser != null) {
            // Create a new connection in Firestore (e.g., in a "connections" collection)
            val connectionRef = db.collection("connections").document()

            val connection = mapOf(
                "user1Id" to currentUser.userID,  // Access userID from your custom User class
                "user2Id" to userId
            )

            connectionRef.set(connection)
                .addOnSuccessListener {
                    Log.d("🔥Firestore", "Connection created successfully between ${currentUser.userID} and $userId")
                }
                .addOnFailureListener { e ->
                    Log.e("🔥Firestore", "Failed to create connection: ${e.message}")
                }
        } else {
            Log.e("🔥Firestore", "Current user is null, cannot create connection")
        }
    }
    fun getCurrentUser(uid: String): User? {
        val userRef = db.collection("users").document(uid)
        var currentUser: User? = null

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                currentUser = document.toObject(User::class.java) // This should return the User object
            } else {
                Log.e("PorociloLovecViewModel", "User not found in Firestore")
            }
        }.addOnFailureListener { e ->
            Log.e("PorociloLovecViewModel", "Error fetching user: ${e.message}")
        }

        return currentUser
    }

    // StateFlow za uporabnike in stanje nalaganja
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Inicializiramo nalaganje uporabnikov ob zagonu ViewModel-a
        getUsers()
    }

    // Funkcija za pridobivanje uporabnikov iz Firestore
    private fun getUsers() {
        _isLoading.value = true // Nastavimo nalaganje na True
        viewModelScope.launch {
            try {
                val usersList = getUsersFromFirestore() // Pridobivanje uporabnikov
                _users.value = usersList // Posodobimo seznam uporabnikov
            } catch (e: Exception) {
                Log.e("PorociloLovecViewModel", "Error fetching users: ${e.message}")
            } finally {
                _isLoading.value = false // Končamo nalaganje
            }
        }
    }

    // Funkcija za asinhrono pridobivanje uporabnikov iz Firestore
    private suspend fun getUsersFromFirestore(): List<User> {
        val usersCollection = db.collection("users") // Reference na kolekcijo "users" v Firestore

        // Pridobivanje vseh dokumentov iz Firestore kolekcije "users"
        val result = usersCollection.get().await() // Uporabimo `await()` za asinhron dostop do podatkov

        // Mapiranje dokumentov na seznam uporabnikov
        return result.documents.mapNotNull { document ->
            try {
                document.toObject(User::class.java)?.apply {
                    userID = document.id // Nastavimo ID dokumenta kot uporabniški ID
                }
            } catch (e: Exception) {
                Log.e("PorociloLovecViewModel", "Error parsing user data: ${e.message}", e)
                null // V primeru napake vrnemo null
            }
        }
    }
}
