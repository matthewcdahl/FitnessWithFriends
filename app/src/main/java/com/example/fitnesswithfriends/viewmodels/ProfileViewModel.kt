package com.example.fitnesswithfriends.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.fitnesswithfriends.R
import com.example.fitnesswithfriends.firebase.FirebaseRepository
import com.example.fitnesswithfriends.models.User
import com.example.fitnesswithfriends.models.Workout
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    // This holds the list of workouts to be shown in the UI
    var user = mutableStateOf(User())
    val db = FirebaseFirestore.getInstance()

    init {
        fetchUser()
    }

    private fun fetchUser() {


    }
}

