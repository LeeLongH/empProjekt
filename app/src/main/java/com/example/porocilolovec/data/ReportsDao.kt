package com.example.porocilolovec.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.porocilolovec.ui.Reports
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow


@Dao
interface ReportDao {
    fun insertReport(report: Reports, callback: (Boolean) -> Unit) {
        val reportsRef = FirebaseDatabase.getInstance().getReference("reports")
        val newReportRef = reportsRef.push() // Generates a unique ID

        newReportRef.setValue(report).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun getReportsByUser(userId: String, managerID: String, callback: (List<Reports>) -> Unit) {
        val reportsRef = FirebaseDatabase.getInstance().getReference("reports")

        reportsRef.orderByChild("userID").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reports = mutableListOf<Reports>()
                    for (child in snapshot.children) {
                        val report = child.getValue(Reports::class.java)
                        if (report?.managerID?.toString() == managerID) { // ✅ Convert Int? to String
                            reports.add(report)
                        }
                    }
                    callback(reports)
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    fun getReportsByUser(userId: String, callback: (List<Reports>) -> Unit) {
        val reportsRef = FirebaseDatabase.getInstance().getReference("reports").orderByChild("userID").equalTo(userId)

        reportsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reports = mutableListOf<Reports>()
                for (child in snapshot.children) {
                    val report = child.getValue(Reports::class.java)
                    report?.let { reports.add(it) }
                }
                callback(reports)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList()) // Handle errors
            }
        })
    }

    fun deleteReport(reportId: String, callback: (Boolean) -> Unit) {
        val reportRef = FirebaseDatabase.getInstance().getReference("reports").child(reportId)

        reportRef.removeValue().addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun updateReport(reportId: String, updatedReport: Reports, callback: (Boolean) -> Unit) {
        val reportRef = FirebaseDatabase.getInstance().getReference("reports").child(reportId)

        reportRef.setValue(updatedReport).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

}
