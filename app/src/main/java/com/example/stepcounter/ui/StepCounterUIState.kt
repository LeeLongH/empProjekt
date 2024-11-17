package com.example.stepcounter.ui

data class StepCounterUIState(
    val stepCount: Int = 4500, // default step count
    val stepGoal: Int = 10000, // default goal
    val user: User? = null // User data can be null initially
)

data class User(
    val name: String,
    val surname: String,
    val profession: String
)
