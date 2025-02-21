package com.example.porocilolovec.data

import android.content.Context

class AppContainer(private val context: Context) {
    val userRepo: OfflineUserRepository by lazy {
        val database = RoomDB.getDatabase(context) // ✅ Get the database instance
        OfflineUserRepository(database.userDao(), database.reportDao()) // ✅ Pass both DAOs
    }
}
