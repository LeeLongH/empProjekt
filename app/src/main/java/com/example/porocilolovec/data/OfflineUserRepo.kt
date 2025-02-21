package com.example.porocilolovec.data

import com.example.porocilolovec.ui.ReportEntity
import com.example.porocilolovec.ui.User

class OfflineUserRepository(private val userDao: UserDao, private val reportDao: ReportDao) {

    suspend fun addUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun addReport(userId: Int, report: ReportEntity) {
        val reportEntity = ReportEntity(
            userId = userId,
            text = report.text,
            timestamp = report.timestamp,
            kilometers = report.kilometers,
            patronReply = report.patronReply
        )
        reportDao.insertReport(reportEntity)
    }

    suspend fun getReportsByUser(userId: Int): List<ReportEntity> {
        return reportDao.getReportsByUser(userId) // ✅ Just return it directly
    }

    suspend fun getUsersByProfession(profession: String): List<User> {
        return userDao.getUsersByProfession(profession)
    }


}
