package com.example.fitnesswithfriends.models

data class User(
    val name: String = "",
    val image_url: String = "",
    val email: String = "",
    var pastWorkouts: List<PastWorkout>? = null
)