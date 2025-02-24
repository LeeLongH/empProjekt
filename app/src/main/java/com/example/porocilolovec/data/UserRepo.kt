package com.example.porocilolovec.data

import com.example.porocilolovec.ui.Reports
import com.example.porocilolovec.ui.User


class UserRepository(private val offlineRepo: OfflineRepo) {

    suspend fun registerUser(user: User) {
        offlineRepo.addUser(user)
    }

    suspend fun fetchUser(userId: Int): User? {
        return offlineRepo.getUserById(userId)
    }


    suspend fun sendReport(userId: Int, report: Reports) {
        offlineRepo.addReport(userId, report)
    }

    suspend fun getUserReports(userId: Int): List<Reports> {
        return offlineRepo.getReportsByUser(userId)
    }
}
