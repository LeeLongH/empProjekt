package com.example.porocilolovec.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.porocilolovec.ui.Reports
import kotlinx.coroutines.flow.Flow


@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: Reports)

    @Query("SELECT * FROM Reports WHERE userID = :userId")
    fun getReportsByUser(userId: Int): Flow<List<Reports>>  // ✅ Must return Flow<List<Reports>>

    @Query("DELETE FROM Reports WHERE reportID = :reportId")
    suspend fun deleteReport(reportId: Int)

}
