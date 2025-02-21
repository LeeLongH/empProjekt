package com.example.porocilolovec.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.porocilolovec.ui.ReportEntity

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity): Long

    @Query("SELECT * FROM ReportEntity WHERE userId = :userId")
    suspend fun getReportsByUser(userId: Int): List<ReportEntity>

    @Delete
    suspend fun deleteReport(report: ReportEntity)
}