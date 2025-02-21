package com.example.porocilolovec.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.porocilolovec.ui.Converters
import com.example.porocilolovec.ui.User
import com.example.porocilolovec.ui.ReportEntity

@Database(entities = [User::class, ReportEntity::class], version = 1)
@TypeConverters(Converters::class) // ✅ Register Converters
abstract class RoomDB : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDatabase(context: Context): RoomDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "users"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}