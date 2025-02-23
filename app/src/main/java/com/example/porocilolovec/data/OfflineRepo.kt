package com.example.porocilolovec.data

import android.content.Context
import android.util.Log
import com.example.porocilolovec.ui.ReportEntity
import com.example.porocilolovec.ui.User

class OfflineRepo(
    private val userDao: UserDao,
    private val reportDao: ReportDao
) {

    suspend fun addUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun addReport(userId: Int, report: ReportEntity) {
        // Insert the new report into the reports table
        reportDao.insertReport(report)

        // Fetch the user
        val user = userDao.getUserById(userId)
        if (user != null) {
            // Update the reports map
            val updatedReports = user.reports.toMutableMap()
            val userReportsList = updatedReports[userId]?.toMutableList() ?: mutableListOf()
            userReportsList.add(report)
            updatedReports[userId] = userReportsList

            // Save the updated user entity
            val updatedUser = user.copy(reports = updatedReports)
            userDao.updateUser(updatedUser)
        }
    }

    suspend fun getReportsByUser(userId: Int): List<ReportEntity> {
        return reportDao.getReportsByUser(userId)
    }

    suspend fun getUsersByProfession(profession: String): List<User> {
        return userDao.getUsersByProfession(profession)
    }

    suspend fun addFriend(userId1: Int, userId2: Int) {
        val user1 = userDao.getUserById(userId1)
        val user2 = userDao.getUserById(userId2)

        if (user1 != null && user2 != null) {

            val updatedReports1 = user1.reports.toMutableMap().apply {
                put(userId2, this[userId2] ?: emptyList())
            }

            val updatedReports2 = user2.reports.toMutableMap().apply {
                put(userId1, this[userId1] ?: emptyList())
            }

            val updatedUser1 = user1.copy(reports = updatedReports1)
            val updatedUser2 = user2.copy(reports = updatedReports2)

            userDao.updateUser(updatedUser1)
            userDao.updateUser(updatedUser2)

            Log.d("DEBUG", "Updated User1: $updatedUser1")
            Log.d("DEBUG", "Updated User2: $updatedUser2")
        } else {
            Log.e("DEBUG", "User not found")
        }
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.getUserByEmailAndPassword(email, password) // Preveri tako email kot geslo
    }

    // Clear session data from SharedPreferences
    fun clearSession(context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}