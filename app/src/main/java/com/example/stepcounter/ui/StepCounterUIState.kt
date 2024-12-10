package com.example.stepcounter.ui
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

data class StepCounterUIState(
    val stepCount: Int = 4500, // default step count
    val stepGoal: Int = 10000, // default goal
    val user: User? = null // User data can be null initially
)

@Entity(tableName = "users_db")
data class User(
    @PrimaryKey(autoGenerate = true)    val id: Int = 0,
    @ColumnInfo(name = "name")          val name: String,
    @ColumnInfo(name = "surname")       val surname: String,
    @ColumnInfo(name = "profession")    val profession: String,
    @ColumnInfo(name = "email")         val email: String
)
