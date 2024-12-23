package com.example.stepcounter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stepcounter.ui.User

@Database (
    entities = [User::class],
    version = 1,
    exportSchema = false
)
abstract class RoomDB : RoomDatabase() {
    abstract fun UserDAO(): UserDAO

    companion object {
        @Volatile
        private var Instance: RoomDB? = null

        fun getDatabase(context: Context): RoomDB {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, RoomDB::class.java, "users_db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}