package com.example.fitnesswithfriends.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.fitnesswithfriends.models.Workout
import com.google.firebase.firestore.FirebaseFirestore

class WorkoutsViewModel : ViewModel() {

    // This holds the list of workouts to be shown in the UI
    var workouts = mutableStateOf<List<Workout>>(emptyList())
    var isLoading = mutableStateOf(true)
    val db = FirebaseFirestore.getInstance()
    var selectedWorkout: Workout? = null

    init {
        // Simulate fetching workouts from a repository or database
        fetchWorkouts()
    }

    private fun fetchWorkouts() {
        db.collection("workouts")
            .get()
            .addOnSuccessListener { result ->
                val workoutList = mutableListOf<Workout>()
                for (document in result) {
                    val workout = Workout(
                        name = document.getString("name") ?: "",
                        image_url = document.getString("image_url") ?: "",
                        duration = document.getString("duration") ?: "",
                        description = document.getString("description") ?: "",
                        id = document.id
                    )
                    workoutList.add(workout)
                }
                workouts.value = workoutList
                isLoading.value = false
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents.", exception)
            }
    }

    fun putSelectedWorkout(workout: Workout){
        this.selectedWorkout = workout
    }

    fun grabSelectedWorkout(): Workout{
        return selectedWorkout ?: Workout(name = "Default", duration = "Default", image_url = "Default")
    }
}

