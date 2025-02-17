package com.example.porocilolovec.ui
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "users_db")
data class User(
    @PrimaryKey(autoGenerate = true)    val id: Int = 0,
    @ColumnInfo(name = "name")          val name: String,
    @ColumnInfo(name = "surname")       val surname: String,
    @ColumnInfo(name = "profession")    val profession: String,
    @ColumnInfo(name = "email")         val email: String
)
