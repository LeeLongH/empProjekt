package com.example.porocilolovec.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class PorociloLovecViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    /*private val _workRequests = MutableStateFlow("")
    val workRequests: StateFlow<String> get() = _workRequests*/

    private val _workRequests = MutableStateFlow<String>("")
    val workRequests: StateFlow<String> = _workRequests

    private val _usersByIds = MutableStateFlow<List<User>>(emptyList())
    val usersByIds: StateFlow<List<User>> get() = _usersByIds

    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId

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
            apply()
        }
        Log.d("PorociloLovecViewModel", "preferences USER_ID: ${user.userID}")

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

    // WORK REQUEST
    fun sendWorkRequest(context: Context, targetUserId: String) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val currentUserId = sharedPreferences.getString("USER_ID", null)

        // Ensure currentUserId is not null or empty
        if (currentUserId.isNullOrEmpty()) {
            Log.e("PorociloLovecViewModel", "❌ Current user ID is null or empty")
            return
        }

        // Log the currentUserId to check its value
        Log.d("PorociloLovecViewModel", "Current user ID: $currentUserId")

        // Proceed with the transaction
        val userRef = db.collection("users").document(targetUserId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)

            if (!snapshot.exists()) {
                Log.e(
                    "PorociloLovecViewModel",
                    "❌ Target user document does not exist for ID: $targetUserId"
                )
                return@runTransaction
            }

            val currentRequests = snapshot.getString("workRequests") ?: ""
            Log.d("PorociloLovecViewModel", "Current workRequests: $currentRequests")

            // Check if currentRequests is empty and append currentUserId
            val updatedRequests = if (currentRequests.isEmpty()) {
                val newRequests = currentUserId
                Log.d(
                    "PorociloLovecViewModel",
                    "Current workRequests is empty, setting it to: $newRequests"
                )
                newRequests
            } else if (currentRequests.split(" ").contains(currentUserId)) {
                Log.d("PorociloLovecViewModel", "Current user already in workRequests")
                currentRequests // Already added
            } else {
                val newRequests = "$currentRequests $currentUserId".trim()
                Log.d("PorociloLovecViewModel", "Updated workRequests: $newRequests")
                newRequests
            }

            // Update the workRequests field
            transaction.update(userRef, "workRequests", updatedRequests)

        }.addOnSuccessListener {
            Log.d("PorociloLovecViewModel", "✅ Work request sent to user $targetUserId")
        }.addOnFailureListener { e ->
            Log.e("PorociloLovecViewModel", "❌ Failed to send work request: ${e.message}")
        }
    }

    fun getWorkRequests(context: Context) {
        // Assuming you're using Firebase Firestore
        val userId = getCurrentUserId(context)  // Retrieve user ID from SharedPreferences or elsewhere

        // Fetch the user's work requests from Firestore (replace with actual logic)
        val userDoc = Firebase.firestore.collection("users").document(userId.toString())
        userDoc.get().addOnSuccessListener { document ->
            val workRequestsString = document.getString("workRequests") ?: ""
            _workRequests.value = workRequestsString  // Update the work requests string
        }
    }


    // Reject a work request and update the workRequests field
    fun rejectWorkRequest(context: Context, requestToRemove: String) {
        val currentUserId = getCurrentUserId(context)

        if (currentUserId == null) {
            Log.e("PorociloLovecViewModel", "❌ User is not logged in.")
            return
        }

        val userRef = db.collection("users").document(getCurrentUserId(context).toString())

        // Remove the request from the workRequests field
        userRef.update("workRequests", FieldValue.arrayRemove(requestToRemove))
            .addOnSuccessListener {
                Log.d("PorociloLovecViewModel", "✅ Work request removed from user: $requestToRemove")
            }
            .addOnFailureListener { e ->
                Log.e("PorociloLovecViewModel", "❌ Failed to remove work request: ${e.message}", e)
            }
    }

    fun acceptWorkRequest(context: Context, targetUserId: String) {
        val currentUserId = getCurrentUserId(context)  // Use context here to get the current user ID

        if (currentUserId == null) {
            Log.e("PorociloLovecViewModel", "❌ User is not logged in.")
            return
        }

        // Create a connection in the "connections" collection
        val connectionRef = db.collection("connections").document()

        // Add the connection between manager (current user) and worker (target user)
        val connection = Connections(connectionID = connectionRef.id, managerID = getCurrentUserId(context).toString(), workerID = targetUserId)

        connectionRef.set(connection)
            .addOnSuccessListener {
                Log.d("PorociloLovecViewModel", "✅ Connection added between $currentUserId and $targetUserId")
                // After creating connection, remove the work request from the user's field
                rejectWorkRequest(context, targetUserId)  // Pass context and targetUserId here
            }
            .addOnFailureListener { e ->
                Log.e("PorociloLovecViewModel", "❌ Failed to create connection: ${e.message}", e)
            }
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

    // Fetch user ID from SharedPreferences
    fun getCurrentUserId(context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("USER_ID", "") ?: ""
        _currentUserId.value = userId
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











}

