package com.example.porocilolovec.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.porocilolovec.ui.Connections

@Dao
interface ConnectionsDao {

    @Insert
    suspend fun insertConnection(connection: Connections)

    // You can also have a function to fetch connections if needed
    @Query("SELECT * FROM connections WHERE userID = :userId OR employeeID = :userId")
    suspend fun getUserConnections(userId: Int): List<Connections>
}
