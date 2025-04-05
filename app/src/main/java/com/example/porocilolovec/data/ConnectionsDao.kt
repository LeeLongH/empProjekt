package com.example.porocilolovec.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.porocilolovec.ui.Connections
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Dao
interface ConnectionsDao {

    fun getConnection(managerId: String, workerId: String, callback: (String?) -> Unit) {
        val connectionsRef = FirebaseDatabase.getInstance().getReference("connections")

        connectionsRef.orderByChild("managerId").equalTo(managerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val workerIdInDb = child.child("workerID").getValue(String::class.java)
                        if (workerIdInDb == workerId) {
                            callback(child.key) // connectionID
                            return
                        }
                    }
                    callback(null) // No match found
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    fun insertConnection(connection: Connections, callback: (Boolean) -> Unit) {
        val connectionsRef = FirebaseDatabase.getInstance().getReference("connections")
        val newConnectionRef = connectionsRef.push() // Generates a unique ID

        newConnectionRef.setValue(connection).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun getConnectionsForWorker(workerId: String, callback: (List<Connections>) -> Unit) {
        val connectionsRef = FirebaseDatabase.getInstance().getReference("connections")
            .orderByChild("workerID").equalTo(workerId)

        connectionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connections = mutableListOf<Connections>()
                for (child in snapshot.children) {
                    val connection = child.getValue(Connections::class.java)
                    connection?.let { connections.add(it) }
                }
                callback(connections)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun getManagerIdsForWorker(workerID: String, callback: (List<String>) -> Unit) {
        val connectionsRef = FirebaseDatabase.getInstance().getReference("connections")
            .orderByChild("workerID").equalTo(workerID)

        connectionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val managerIds = mutableListOf<String>()
                for (child in snapshot.children) {
                    val managerId = child.child("managerID").getValue(String::class.java)
                    managerId?.let { managerIds.add(it) }
                }
                callback(managerIds)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun getHunterIdsForManager(managerID: String, callback: (List<String>) -> Unit) {
        val connectionsRef = FirebaseDatabase.getInstance().getReference("connections")
            .orderByChild("managerID").equalTo(managerID)

        connectionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val workerIds = mutableListOf<String>()
                for (child in snapshot.children) {
                    val workerId = child.child("workerID").getValue(String::class.java)
                    workerId?.let { workerIds.add(it) }
                }
                callback(workerIds)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

}