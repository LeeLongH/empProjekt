package com.example.porocilolovec.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.porocilolovec.ui.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@Dao
interface UserDao {

    fun getUserNameById(userId: String, callback: (String?) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.child("fullName").get().addOnSuccessListener { snapshot ->
            callback(snapshot.getValue(String::class.java))
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun getUserByEmailAndPassword(email: String, password: String, callback: (User?) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        usersRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val user = child.getValue(User::class.java)
                        if (user?.password == password) { // Manual password check
                            callback(user)
                            return
                        }
                    }
                    callback(null) // No matching user found
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    fun searchUsersByProfession(profession: String, callback: (List<User>) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        usersRef.orderByChild("profession")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = mutableListOf<User>()
                    for (child in snapshot.children) {
                        val user = child.getValue(User::class.java)
                        // Check if user is not null and the profession doesn't match
                        if (user != null && user.profession != profession) {
                            users.add(user)
                        }
                    }
                    callback(users)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList()) // Handle any cancellation or error
                }
            })
    }

    fun clearUsers(callback: (Boolean) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        usersRef.removeValue().addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun getWorkRequests(userId: String, callback: (String?) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.child("workRequests").get().addOnSuccessListener { snapshot ->
            callback(snapshot.getValue(String::class.java))
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun addWorkRequests(userId: String, newRequest: String, callback: (Boolean) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.child("workRequests").get().addOnSuccessListener { snapshot ->
            val existingRequests = snapshot.getValue(String::class.java) ?: ""
            val updatedRequests = "$existingRequests $newRequest".trim()

            userRef.child("workRequests").setValue(updatedRequests).addOnSuccessListener {
                callback(true)
            }.addOnFailureListener {
                callback(false)
            }
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun updateWorkRequests(userId: String, updatedRequests: String, callback: (Boolean) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.child("workRequests").setValue(updatedRequests).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun getUsersByIds(userIds: List<String>, callback: (List<User>) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        val users = mutableListOf<User>()

        var remaining = userIds.size
        if (remaining == 0) {
            callback(emptyList())
            return
        }

        for (userId in userIds) {
            usersRef.child(userId).get().addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                user?.let { users.add(it) }

                remaining--
                if (remaining == 0) callback(users) // Return when all queries finish
            }.addOnFailureListener {
                remaining--
                if (remaining == 0) callback(users)
            }
        }
    }

    fun updateUser(user: User, callback: (Boolean) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user.userID.toString())

        userRef.setValue(user).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun getAllUsers(callback: (List<User>) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    user?.let { users.add(it) }
                }
                callback(users)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList()) // Handle errors
            }
        })
    }

    fun insertUser(user: User, callback: (Boolean) -> Unit) {
        val userId = user.userID.toString()
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.setValue(user).addOnSuccessListener {
            callback(true) // Success
        }.addOnFailureListener {
            callback(false) // Failure
        }
    }

    fun getUserById(userId: String, callback: (User?) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(User::class.java)
            callback(user)
        }.addOnFailureListener {
            callback(null)
        }
    }

}
