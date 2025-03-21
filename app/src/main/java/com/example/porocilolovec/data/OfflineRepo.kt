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

    suspend fun addUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    suspend fun getUsersByIds(userIds: List<Int>): List<User> {
        return userDao.getUsersByIds(userIds)
    }

    suspend fun addReport(userId: Int, report: Reports) {
        reportDao.insertReport(report)
    }

    suspend fun searchUsersByProfession(profession: String): List<User> {
        return userDao.searchUsersByProfession(profession)
    }

    suspend fun getUserByEmailAndPassword(email: String, password: String): User? {
        return userDao.getUserByEmailAndPassword(email, password)
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.getUserByEmailAndPassword(email, password)
    }

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
            Log.d("BBB", "Inserting new connection for user $currentUserId and worker $targetUserId")
            connectionsDao.insertConnection(Connections(managerID = currentUserId, workerID = targetUserId))
        } else {
            // If the connection already exists, log a message (or handle it as needed)
            Log.d("BBB", "Connection already exists for user $currentUserId and worker $targetUserId")
        }
    }

    suspend fun getManagerIdsForHunter(workerID: Int): List<Int> {
        return connectionsDao.getManagerIdsForHunter(workerID)
    }

    suspend fun getHunterIdsForManager(managerID: Int): List<Int> {
        return connectionsDao.getHunterIdsForManager(managerID)
    }












    suspend fun getConnection(managerID: Int, workerID: Int): Int? {
        return connectionsDao.getConnection(managerID, workerID) // Returns connection ID or null
    }

    suspend fun submitReport(
        userID: Int,
        selectedManagerID: Int, // Pass the connection ID (can be null)
        text: String,
        distance: Float,
        timeOnTerrain: Int
    ) {
        val report = Reports(
            userID = userID,
            managerID = selectedManagerID, // Store the connection ID
            timestamp = System.currentTimeMillis(),
            text = text,
            distance = distance,
            timeOnTerrain = timeOnTerrain,
            response = ""
        )
        reportDao.insertReport(report) // Insert the report into the database
    }



    fun getReportsForUser(userID: Int, managerID: Int): Flow<List<Reports>> {
        return reportDao.getReportsByUser(userID, managerID)
    }
    fun getReportsHistory(userID: Int): Flow<List<Reports>> {
        return reportDao.getReportsHistory(userID)
    }

    suspend fun deleteReport(reportID: Int) {
        reportDao.deleteReport(reportID)
    }

    suspend fun updateReport(report: Reports) {
        reportDao.update(report)
    }


}