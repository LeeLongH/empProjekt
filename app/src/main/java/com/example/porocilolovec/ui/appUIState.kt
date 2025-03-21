package com.example.porocilolovec.ui
import androidx.room.Entity
import androidx.room.ForeignKey
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
    @PrimaryKey(autoGenerate = true) val reportID: Int = 0, // Auto-generated ID
    val managerID: Int, // 🔥 Store only the foreign key (not the whole object)
    val userID: Int,
    val timestamp: Long,
    val text: String,
    val distance: Float,
    val timeOnTerrain: Int,
    val response: String = "[]" // Shranimo kot JSON array string
){
    fun getResponseList(): List<ChatMessage> {
        return Gson().fromJson(response, object : TypeToken<List<ChatMessage>>() {}.type) ?: emptyList()
    }

    fun addResponseMessage(message: ChatMessage): Reports {
        val messages = getResponseList().toMutableList()
        messages.add(message)
        return copy(response = Gson().toJson(messages))
    }
}

data class ChatMessage(
    val sender: String, // "Manager" ali "Guard"
    val message: String,
    val timestamp: Long
)

@Entity(
    tableName = "Connections",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userID"],
            childColumns = ["managerID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userID"],
            childColumns = ["workerID"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("managerID"),
        Index("workerID")
    ]
)
data class Connections(
    @PrimaryKey(autoGenerate = true) val connectionID: Int = 0,
    val managerID: Int, // Foreign key referencing User.userID (the "manager")
    val workerID: Int // Foreign key referencing User.userID (the "worker")
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
