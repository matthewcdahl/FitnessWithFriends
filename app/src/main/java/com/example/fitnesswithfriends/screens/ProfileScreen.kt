package com.example.fitnesswithfriends.screens


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.fitnesswithfriends.ui.theme.FitnessWithFriendsTheme
import com.example.fitnesswithfriends.R
import com.example.fitnesswithfriends.firebase.FirebaseRepository
import com.example.fitnesswithfriends.models.PastWorkout
import com.example.fitnesswithfriends.models.User
import com.example.fitnesswithfriends.models.Workout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(user: User, isVisible: Boolean) {

    val navBackStackEntry by rememberUpdatedState(LocalLifecycleOwner.current.lifecycle)
    var mutableUser by remember { mutableStateOf(user) }

    LaunchedEffect(isVisible) {
        mutableUser = FirebaseRepository.refreshUser(user) ?: user
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = Color.White)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.LightGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues(
                    top = innerPadding.calculateTopPadding() + 5.dp,
                    start = innerPadding.calculateLeftPadding(LayoutDirection.Ltr) + 4.dp
                )),

        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = mutableUser.image_url,
                    contentDescription = "User Profile Image",
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.pushup),
                    modifier = Modifier
                        .height(150.dp)
                        .width(150.dp)
                        .clip(CircleShape)
                        .border(5.dp, Color.Black, CircleShape),

                    )

                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Text(
                        mutableUser.name,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                    Row(){
                        Icon(
                            painter = painterResource(id = R.drawable.fitness_center_24px),
                            contentDescription = "Dumbbell",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        val workoutCount = mutableUser.pastWorkouts?.count() ?: 0
                        Text(
                            text = "$workoutCount Workout${if (workoutCount == 1) "" else "s"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                            fontStyle = FontStyle.Italic,
                            //fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Past Workouts", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(mutableUser.pastWorkouts.orEmpty()) { pWo ->
                    PastWorkoutCard(pWo)
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastWorkoutCard(pastWorkout: PastWorkout) {

    val coroutineScope = rememberCoroutineScope()

    // Load workout details from Firebase
    val workoutDetails by produceState<Workout?>(initialValue = null, pastWorkout) {
        value = FirebaseRepository.getWorkoutDetails(pastWorkout.workout_id)
    }

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
                model = workoutDetails?.image_url ?: "Default",
                contentDescription = "Workout Image",
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.pushup),
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
                            text = workoutDetails?.name ?: "Default",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )


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
                            text = "${pastWorkout.duration} minutes",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            fontStyle = FontStyle.Italic
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "${pastWorkout.sets} sets of ${pastWorkout.reps}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.fitness_center_24px),
                            contentDescription = "Hourglass",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp).align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${pastWorkout.total_pounds} lbs",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontStyle = FontStyle.Italic,
                            //fontWeight = FontWeight.Bold,
                        )
                    }

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingProfile() {
    FitnessWithFriendsTheme {
        Profile(User(name = "John Bob", image_url = "", email = "mattdahl3@gmail.com"), isVisible = true)
    }
}