package com.example.stepcounter.ui
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

data class StepCounterUIState(
    val stepCount: Int = 4500, // default step count
    val stepGoal: Int = 10000, // default goal
    val user: User? = null // User data can be null initially
)

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val surname: String,
    val profession: String,
    val email: String
)
