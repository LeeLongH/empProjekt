package com.example.porocilolovec.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.porocilolovec.ui.Connections

@Dao
interface ConnectionsDao {

    @Query("SELECT connectionID FROM Connections WHERE managerId = :managerId AND workerID = :workerId")
    suspend fun getConnection(managerId: Int, workerId: Int): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertConnection(connection: Connections)

    //@Query("SELECT * FROM Connections WHERE managerID = :managerID")
    //suspend fun getConnectionsForManager(managerId: Int): List<Connections>

    @Query("SELECT * FROM Connections WHERE workerID = :workerId")
    suspend fun getConnectionsForWorker(workerId: Int): List<Connections>

    //@Query("DELETE FROM Connections WHERE managerID = :managerID AND workerID = :workerId")
    //suspend fun deleteConnection(managerId: Int, workerId: Int)

    @Query("SELECT managerID FROM Connections WHERE workerID = :workerID")
    suspend fun getManagerIdsForHunter(workerID: Int): List<Int>

    @Query("SELECT workerID FROM Connections WHERE managerID = :managerID")
    suspend fun getHunterIdsForManager(managerID: Int): List<Int>

}