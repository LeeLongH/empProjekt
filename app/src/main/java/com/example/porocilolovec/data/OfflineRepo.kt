package com.example.porocilolovec.data

import android.content.Context
import android.util.Log
import com.example.porocilolovec.ui.Connections
//import androidx.preference.contains
import com.example.porocilolovec.ui.Reports
import com.example.porocilolovec.ui.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class OfflineRepo(
    private val userDao: UserDao,
    private val reportDao: ReportDao,
    private val connectionsDao: ConnectionsDao,
    private val context: Context  // Add context to the constructor

) {

    // 📌 Dodaj uporabnika v bazo
    suspend fun addUser(user: User) {
        userDao.insertUser(user)
    }

    // 📌 Pridobi uporabnika po ID-ju
    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    // Function to get users by their IDs
    suspend fun getUsersByIds(userIds: List<Int>): List<User> {
        return userDao.getUsersByIds(userIds)
    }

    // 📌 Dodaj poročilo in ga shrani v tabelo reports
    suspend fun addReport(userId: Int, report: Reports) {
        reportDao.insertReport(report)
    }

    // 📌 Pridobi poročila določenega uporabnika
    /*suspend fun getReportsByUser(userId: Int): List<Reports> {
        return reportDao.getReportsByUser(userId)
    }*/

    // 📌 searchUsersBYProfessionScreen
    suspend fun searchUsersByProfession(profession: String): List<User> {
        return userDao.searchUsersByProfession(profession)
    }


    // 📌 Prijava uporabnika
    suspend fun loginUser(email: String, password: String): User? {
        return userDao.getUserByEmailAndPassword(email, password)
    }

    // 📌 Odjava uporabnika - briše shranjene podatke seje
    fun clearSession(context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }


    suspend fun registerUser(fullName: String, email: String, password: String, profession: String) {
        // Create a User object with an empty list of workRequests
        val user = User(
            fullName = fullName,
            email = email,
            password = password,
            profession = profession,
            workRequests = ""
        )

        // Insert the user into the database
        userDao.insertUser(user)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    suspend fun clearUserTable() {
        userDao.clearTable()  // Calling the DAO method to clear the table
    }

    // Get the workRequests for the user as a string
    suspend fun getWorkRequests(userId: Int): String? {
        return userDao.getWorkRequests(userId)
    }

    // Update the workRequests with the new string
    suspend fun addWorkRequests(userId: Int, workRequests: String) {
        userDao.addWorkRequests(userId, workRequests)
    }
    suspend fun sendWorkRequest(currentUserId: Int, newWorkRequests: String) {
        // Update the current user's workRequests with the new string
        userDao.addWorkRequests(currentUserId, newWorkRequests)
    }

    // Insert a new connection
    suspend fun insertConnection(connection: Connections) {
        connectionsDao.insertConnection(connection)
    }
    fun saveUpdatedWorkRequests(workRequests: String) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("WORK_REQUESTS", workRequests).apply()
    }


    suspend fun rejectWorkRequest(userId: Int, requestToRemove: String) {
        val currentRequests = userDao.getWorkRequests(userId) ?: ""

        // Razbij workRequests v seznam, odstrani željeno zahtevo, nato ponovno združi v string
        val updatedRequests = currentRequests.split(" ")
            .filter { it.isNotEmpty() && it != requestToRemove }
            .joinToString(" ")

        // Posodobi bazo z novo vrednostjo
        userDao.updateWorkRequests(userId, updatedRequests)
    }


    suspend fun acceptWorkRequest(currentUserId: Int, targetUserId: Int) {
        // Check if the connection already exists
        val existingConnection = connectionsDao.getConnection(currentUserId, targetUserId)

        if (existingConnection == null) {
            // If the connection doesn't exist, insert a new one
            Log.d("WORKD", "Inserting new connection for user $currentUserId and worker $targetUserId")
            connectionsDao.insertConnection(Connections(userID = currentUserId, workerID = targetUserId))
        } else {
            // If the connection already exists, log a message (or handle it as needed)
            Log.d("WORKD", "Connection already exists for user $currentUserId and worker $targetUserId")
        }
    }

    suspend fun getManagerIdsForHunter(workerID: Int): List<Int> {
        return connectionsDao.getManagerIdsForHunter(workerID)
    }












    suspend fun getConnection(managerId: Int, workerId: Int): Int? {
        return connectionsDao.getConnection(managerId, workerId) // Returns connection ID or null
    }

    suspend fun submitReport(
        userID: Int,
        connectionID: Int?, // Pass the connection ID (can be null)
        text: String,
        distance: Float,
        timeOnTerrain: Int
    ) {
        val report = Reports(
            userID = userID,
            connectionID = connectionID, // Store the connection ID
            timestamp = System.currentTimeMillis(),
            text = text,
            distance = distance,
            timeOnTerrain = timeOnTerrain
        )
        reportDao.insertReport(report) // Insert the report into the database
    }



    fun getReportsForUser(userID: Int): Flow<List<Reports>> {
        return reportDao.getReportsByUser(userID)
    }


    suspend fun deleteReport(reportID: Int) {
        reportDao.deleteReport(reportID)
    }


}