package com.example.stepcounter.data
import android.content.Context

class AppContainer(private val context: Context) {
    val userRepo: UserRepo by lazy {
        OfflineUserRepo(RoomDB.getDatabase(context).UserDAO())
    }
}
