package com.example.fitnesswithfriends.models

data class PastWorkout(
    val workout_id: String = "",
    val duration: Int = 0,
    val sets: Int = 0,
    val reps: Int = 0,
    val total_pounds: Int = 0
) {
    // No-argument constructor required by Firestore
    constructor() : this(workout_id = "", duration = 0, sets = 0, reps = 0, total_pounds = 0)
}