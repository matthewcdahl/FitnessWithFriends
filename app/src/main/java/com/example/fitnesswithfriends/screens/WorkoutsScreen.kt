package com.example.fitnesswithfriends.screens

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController

import com.example.fitnesswithfriends.R
import com.example.fitnesswithfriends.models.Workout
import com.example.fitnesswithfriends.viewmodels.WorkoutsViewModel
import com.example.fitnesswithfriends.ui.theme.FitnessWithFriendsTheme
import coil3.compose.AsyncImage
import com.example.fitnesswithfriends.models.User


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Workouts(workoutsViewModel: WorkoutsViewModel, navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Workouts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = Color.White)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black// Set the background of the TopAppBar to black
                )
            )
        },
        containerColor = Color.LightGray
    ) { innerPadding ->
        if (workoutsViewModel.isLoading.value) {
            // Show a loading spinner
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 10.dp,
                    bottom = innerPadding.calculateBottomPadding() + 10.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                items(workoutsViewModel.workouts.value) { workout ->
                    WorkoutCard(workout, workoutsViewModel, navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutCard(workout: Workout, workoutVM: WorkoutsViewModel, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Row(
            modifier = Modifier
                //.padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = workout.image_url,
                contentDescription = "Workout Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(110.dp)
                    .width(120.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier.padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth() // Ensures the Row fills the available width
                    ) {
                        Text(
                            text = workout.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            //colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                            onClick = {
                                workoutVM.putSelectedWorkout(workout)
                                navController.navigate("Workout")
                            },
                            modifier = Modifier
                                .width(80.dp)
                                .height(30.dp),
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Text(
                                text = "Start",
                                fontSize = 14.sp, // Set a proper font size for visibility
                                color = Color.White, // Button text color
                                modifier = Modifier
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.hourglass_top_24px),
                            contentDescription = "Hourglass",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = workout.duration,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            fontStyle = FontStyle.Italic
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = workout.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingWorkout() {
    FitnessWithFriendsTheme {
        //Workouts()
        WorkoutCard(
            workout = Workout(),
            workoutVM = viewModel(),
            navController = rememberNavController()
        )
    }
}