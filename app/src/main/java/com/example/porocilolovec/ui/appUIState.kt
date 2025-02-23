package com.example.porocilolovec.ui
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val surname: String,
    val email: String,
    val reports: Map<Int, List<ReportEntity>> = emptyMap(), // ✅ Initialize empty map
    val profession: String,
    val password: String
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,  // Povezava s User
    val text: String,
    val timestamp: Int,
    val kilometers: Float,
    val patronReply: String? = null
)

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromReportMap(value: Map<Int, List<ReportEntity>>?): String {
        return gson.toJson(value) // ✅ Convert Map<Int, List<ReportEntity>> to JSON String
    }

    @TypeConverter
    fun toReportMap(value: String): Map<Int, List<ReportEntity>> {
        val mapType = object : TypeToken<Map<Int, List<ReportEntity>>>() {}.type
        return gson.fromJson(value, mapType) ?: emptyMap() // ✅ Convert JSON String back to Map
    }
}