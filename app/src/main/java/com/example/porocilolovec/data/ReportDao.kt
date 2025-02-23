package com.example.porocilolovec.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.porocilolovec.ui.ReportEntity
import com.example.porocilolovec.ui.User


@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("SELECT * FROM reports WHERE userId = :userId")
    suspend fun getReportsByUser(userId: Int): List<ReportEntity>
}