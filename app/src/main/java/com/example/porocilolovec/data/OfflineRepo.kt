package com.example.porocilolovec.data

import android.content.Context
import android.util.Log
import com.example.porocilolovec.ui.Connections
//import androidx.preference.contains
import com.example.porocilolovec.ui.Reports
import com.example.porocilolovec.ui.User

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


    // 📌 Dodaj poročilo in ga shrani v tabelo reports
    suspend fun addReport(userId: Int, report: Reports) {
        reportDao.insertReport(report)
    }

    // 📌 Pridobi poročila določenega uporabnika
    suspend fun getReportsByUser(userId: Int): List<Reports> {
        return reportDao.getReportsByUser(userId)
    }

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
    suspend fun updateWorkRequests(userId: Int, workRequests: String) {
        userDao.updateWorkRequests(userId, workRequests)
    }
    suspend fun sendWorkRequest(currentUserId: Int, newWorkRequests: String) {
        // Update the current user's workRequests with the new string
        userDao.updateWorkRequests(currentUserId, newWorkRequests)
    }

    // Insert a new connection
    suspend fun insertConnection(connection: Connections) {
        connectionsDao.insertConnection(connection)
    }
    fun saveUpdatedWorkRequests(workRequests: String) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("WORK_REQUESTS", workRequests).apply()
    }

    // Function to get users by their IDs
    suspend fun getUsersByIds(userIds: List<Int>): List<User> {
        return userDao.getUsersByIds(userIds)
    }



}