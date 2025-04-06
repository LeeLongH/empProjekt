package com.example.porocilolovec.ui
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class User(
    var userID: String = "",  // Firebase-generated ID (instead of Int)
    val fullName: String = "",
    val profession: String = "",
    val email: String = "",
    val password: String = "",
    val workRequests: String = ""
)

data class Reports(
    val reportID: String = "",  // Firebase ID instead of Int
    val managerID: String = "", // Reference as String (Firebase key)
    val userID: String = "",
    val timestamp: Long = 0L,
    val text: String = "",
    val distance: Float = 0f,
    val timeOnTerrain: Int = 0,
    val response: String = "[]" // JSON Array as a String
) {
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
    val sender: String = "",  // "Manager" or "Guard"
    val message: String = "",
    val timestamp: Long = 0L
)

data class Connections(
    val connectionID: String = "",  // Firebase-generated ID
    val managerID: String = "",  // Reference as String (Firebase key)
    val workerID: String = ""
)
