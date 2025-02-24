package com.example.porocilolovec.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.porocilolovec.ui.Connections
import com.example.porocilolovec.ui.Converters
import com.example.porocilolovec.ui.User
import com.example.porocilolovec.ui.Reports

@Database(entities = [User::class, Reports::class, Connections::class], version = 1)
@TypeConverters(Converters::class) // Registering the TypeConverter here
abstract class RoomDB : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun connectionDao(): ConnectionDao
    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDatabase(context: Context): RoomDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "DB3"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}