package com.example.porocilolovec.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.porocilolovec.ui.Connections

@Dao
interface ConnectionsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertConnection(connection: Connections)

    @Query("SELECT * FROM Connections WHERE userID = :managerId")
    suspend fun getConnectionsForManager(managerId: Int): List<Connections>

    @Query("SELECT * FROM Connections WHERE workerID = :workerId")
    suspend fun getConnectionsForWorker(workerId: Int): List<Connections>

    @Query("SELECT * FROM Connections WHERE userID = :managerId AND workerID = :workerId")
    suspend fun getConnection(managerId: Int, workerId: Int): Connections?

    @Query("DELETE FROM Connections WHERE userID = :managerId AND workerID = :workerId")
    suspend fun deleteConnection(managerId: Int, workerId: Int)

    @Query("SELECT userID FROM Connections WHERE workerID = :workerID")
    suspend fun getManagerIdsForHunter(workerID: Int): List<Int>

}