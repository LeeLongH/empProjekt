package com.example.porocilolovec.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.example.porocilolovec.ui.ReportEntity
import com.example.porocilolovec.ui.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users WHERE profession = :profession")
    suspend fun getUsersByProfession(profession: String): List<User>

    // New method to add a friend by updating the reports map
    @Transaction
    suspend fun addFriend(currentUserId: Int, friendUserId: Int) {
        // Get the current user and their reports map
        val currentUser = getUserById(currentUserId)

        if (currentUser != null) {
            // Add the friend ID to the reports map with an empty list of reports
            val updatedReports = currentUser.reports.toMutableMap()
            updatedReports[friendUserId] = emptyList() // Initialize empty reports list for the new friend

            // Update the current user with the modified reports map
            val updatedUser = currentUser.copy(reports = updatedReports)
            updateUser(updatedUser)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReport(report: ReportEntity): Long  // Correct signature, passing ReportEntity


}