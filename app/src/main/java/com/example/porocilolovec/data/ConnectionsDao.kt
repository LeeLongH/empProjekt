package com.example.porocilolovec.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.porocilolovec.ui.Connections

@Dao
interface ConnectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnection(connection: Connections)

    @Query("SELECT * FROM connections WHERE userId = :userId")
    suspend fun getConnectionsByUserId(userId: Int): List<Connections>

    @Query("SELECT employeeId FROM connections WHERE userId = :userId")
    suspend fun getEmployeeIdsForUser(userId: Int): List<Int>
}
