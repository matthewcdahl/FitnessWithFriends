package com.example.fitnesswithfriends.firebase

import com.example.fitnesswithfriends.models.PastWorkout
import com.example.fitnesswithfriends.models.User
import com.example.fitnesswithfriends.models.Workout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseRepository {



    suspend fun getWorkoutDetails(workoutId: String): Workout? {
        val db = FirebaseFirestore.getInstance() // This is safe: returns the singleton but doesn't leak context

        return try {
            val snapshot = db
                .collection("workouts")
                .document(workoutId)
                .get()
                .await()

            snapshot.toObject(Workout::class.java)
        } catch (e: Exception) {
            println("Error fetching workout details: ${e.message}")
            null
        }
    }

    fun postPastWorkout(user: User, pastWorkout: PastWorkout){
        val db = FirebaseFirestore.getInstance()
        val pastWorkouts = db
            .collection("users")
            .document(user.email) // Access the user's specific document by its ID
            .collection("pastWorkouts")
            .add(pastWorkout)
    }

    suspend fun refreshUser(user: User): User?{
        println("OG ${user?.email}")
        val db = FirebaseFirestore.getInstance()
        return try {
            val snapshot = db
                .collection("users")
                .document(user.email)
                .get()
                .await()

            val pastWorkoutsSnapshot = db
                .collection("users")
                .document(user.email) // Access the user's specific document by its ID
                .collection("pastWorkouts")
                .get()
                .await()

            val pastWorkouts = pastWorkoutsSnapshot.documents.mapNotNull { pastWorkoutDoc ->
                // Assuming PastWorkout is a data class
                pastWorkoutDoc.toObject(PastWorkout::class.java)
            }

            val newUser = snapshot.toObject(User::class.java)
            newUser?.pastWorkouts = pastWorkouts
            println("GOT IT ${newUser?.email}")
            return newUser
        } catch (e: Exception) {
            println("Error fetching workout details: ${e.message}")
            null
        }

    }
}