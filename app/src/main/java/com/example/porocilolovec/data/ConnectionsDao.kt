package com.example.porocilolovec.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.porocilolovec.ui.Connections

@Dao
interface ConnectionsDao {

    @Query("SELECT workersIDs FROM connections WHERE userID = :currentUserId")
    suspend fun getWorkersIds(currentUserId: Int): String?

    @Query("UPDATE connections SET workersIDs= :updatedWorkersIds WHERE userID = :currentUserId")
    suspend fun updateWorkersIds(currentUserId: Int, updatedWorkersIds: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnection(connection: Connections)



}
