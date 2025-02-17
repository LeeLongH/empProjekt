package com.example.porocilolovec.data

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.OnConflictStrategy;
import com.example.porocilolovec.ui.User

@Dao
interface UserDAO {
    // Vstavi enega uporabnika
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    // Posodobi podatke uporabnika
    @Update
    suspend fun update(user: User)

    // Izbriši uporabnika
    @Delete
    suspend fun delete(user: User)

    // Pridobi vse uporabnike
    @Query("SELECT * FROM users_db")
    suspend fun getAllUsers(): List<User>

    // Pridobi uporabnika po ID-ju
    @Query("SELECT * FROM users_db WHERE id = :userId")
    suspend fun getUserById(userId: String): User?

    // Pridobi uporabnika po e-pošti
    @Query("SELECT * FROM users_db WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    // Pridobi vse uporabnike z določeno profesijo
    @Query("SELECT * FROM users_db WHERE profession = :profession")
    suspend fun getUsersByProfession(profession: String): List<User>

    // Izbriši vse uporabnike
    @Query("DELETE FROM users_db")
    suspend fun deleteAllUsers()
}
