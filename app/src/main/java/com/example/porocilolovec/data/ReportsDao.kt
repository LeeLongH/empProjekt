package com.example.porocilolovec.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.porocilolovec.ui.Reports


@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: Reports)

    @Query("SELECT * FROM reports WHERE userId = :userId")
    suspend fun getReportsByUser(userId: Int): List<Reports>

}
