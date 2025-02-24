package com.example.porocilolovec.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.porocilolovec.ui.User


@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE userID = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE profession != :profession")
    suspend fun searchUsersByProfession(profession: String): List<User>

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("DELETE FROM users")  // Adjust "users" to your actual table name
    suspend fun clearTable()

    @Query("SELECT workRequests FROM users WHERE userID = :userId")
    suspend fun getWorkRequests(userId: Int): String?

    @Query("UPDATE users SET workRequests = workRequests || ' ' || :workRequests WHERE userID = :userId")
    suspend fun updateWorkRequests(userId: Int, workRequests: String)


    @Update
    suspend fun updateUser(user: User)
}
