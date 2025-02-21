package com.example.porocilolovec.data

import com.example.porocilolovec.ui.ReportEntity
import com.example.porocilolovec.ui.User


class UserRepository(private val offlineRepo: OfflineRepo) {

    suspend fun registerUser(user: User) {
        offlineRepo.addUser(user)
    }

    suspend fun fetchUser(userId: Int): User? {
        return offlineRepo.getUserById(userId)
    }

    suspend fun getAllUsers(): List<User> {
        return offlineRepo.getAllUsers()
    }

    suspend fun updateUser(user: User) {
        offlineRepo.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        offlineRepo.deleteUser(user)
    }

    suspend fun sendReport(userId: Int, report: ReportEntity) {
        offlineRepo.addReport(userId, report)
    }

    suspend fun getUserReports(userId: Int): List<ReportEntity> {
        return offlineRepo.getReportsByUser(userId)
    }
}
