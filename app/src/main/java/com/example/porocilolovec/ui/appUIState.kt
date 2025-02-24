package com.example.porocilolovec.ui
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



@Entity(tableName = "users")
@TypeConverters(Converters::class) // Add this line
data class User(
    @PrimaryKey(autoGenerate = true) val userID: Int = 0, // Primary key auto-generated
    val fullName: String,
    val profession: String,
    val email: String,
    val password: String,
    val workRequests: String
)

@Entity(
    tableName = "Reports",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userID"],
            childColumns = ["userID"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userID")] // Add an index on userID
)
data class Reports(
    @PrimaryKey(autoGenerate = true) val reportID: Int = 0,
    val userID: Int, // Now a foreign key
    val timestamp: Long,
    val text: String,
    val distance: Float,
    val time: Int
)

@Entity(
    tableName = "Connections",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userID"],
            childColumns = ["userID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userID"],
            childColumns = ["employeeID"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userID", "employeeID"]),
        Index("employeeID") // Add an index on employeeID
    ]
)
data class Connections(
    @PrimaryKey val connectionID: Int = 0,
    val userID: Int, // Now a foreign key
    val employeeID: Int // Now a foreign key
)

class Converters {
    @TypeConverter
    fun fromWorkRequests(workRequests: Set<Int>?): String? {
        return Gson().toJson(workRequests)
    }

    @TypeConverter
    fun toWorkRequests(workRequestsString: String?): Set<Int>? {
        val setType = object : TypeToken<Set<Int>>() {}.type
        return Gson().fromJson(workRequestsString, setType)
    }
}
