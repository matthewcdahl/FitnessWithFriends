package com.example.fitnesswithfriends.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fitnesswithfriends.models.Workout
import com.example.fitnesswithfriends.ui.theme.FitnessWithFriendsTheme
import kotlinx.coroutines.delay
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import com.example.fitnesswithfriends.firebase.FirebaseRepository
import com.example.fitnesswithfriends.firebase.FirebaseRepository.postPastWorkout
import com.example.fitnesswithfriends.models.PastWorkout
import com.example.fitnesswithfriends.models.User
import kotlin.math.min

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(workout: Workout, navController: NavController, user: User) {

    val focusManager = LocalFocusManager.current
    val focusRequester = FocusRequester()

    var elapsedTime by rememberSaveable { mutableStateOf(0) }
    var isChecked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            elapsedTime++
        }
    }

    val minutes = elapsedTime / 60
    val seconds = elapsedTime % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)
    var workoutSets by rememberSaveable {
        mutableStateOf(List(1) { WorkoutSet() })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Title on the left
                        Text(
                            "Push Ups",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterStart) // Left-aligned title
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Timer
                        Text(
                            text = timeString,
                            fontSize = 20.sp,
                            color = Color.White
                        )

                        // Finish Button
                        Button(onClick = {

                            val summary = PastWorkout(
                                workout_id = workout.id,
                                sets = workoutSets.count { it.completed },
                                reps = workoutSets.toMutableList()[0].reps,
                                duration = minutes + 1,
                                total_pounds = workoutSets.sumOf { it.lbs }
                            )

                            FirebaseRepository.postPastWorkout(user = user, pastWorkout = summary)

                            navController.popBackStack()
                        }) {
                            Text("Finish", color = Color.White)
                        }
                    }
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
                .padding(
                    PaddingValues(
                        top = innerPadding.calculateTopPadding() + 5.dp,
                        start = innerPadding.calculateLeftPadding(LayoutDirection.Ltr) + 25.dp,
                        end = innerPadding.calculateLeftPadding(LayoutDirection.Ltr) + 25.dp
                    )
                )
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(20.dp))

//            Button(onClick = {
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=WDIpL0pjun0"))
//                context.startActivity(intent)
//            },) {
//                Text("Watch on YouTube")
//            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                HeaderText("Set")
                Spacer(modifier = Modifier.weight(1f))
                HeaderText("lbs")
                Spacer(modifier = Modifier.weight(1f))
                HeaderText("Reps")
                Spacer(modifier = Modifier.weight(1f))
                Text("Done", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))


            workoutSets.forEachIndexed { index, set ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "${index + 1}",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(40.dp),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    OutlinedTextField(
                        value = set.lbs.toString(),
                        onValueChange = {
                            if (it.length <= 5 && it.all { c -> c.isDigit() }) {
                                workoutSets = workoutSets.toMutableList().also { list ->
                                    list[index] = list[index].copy(lbs = if (it.isEmpty()) 0 else it.toInt())
                                }
                            }
                        },
                        label = { Text("lbs") },
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                        singleLine = true,
                        modifier = Modifier.width(80.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    OutlinedTextField(
                        value = set.reps.toString(),
                        onValueChange = {
                            if (it.length <= 5 && it.all { c -> c.isDigit() }) {
                                workoutSets = workoutSets.toMutableList().also { list ->
                                    list[index] = list[index].copy(reps = if (it.isEmpty()) 0 else it.toInt())
                                }
                            }
                        },
                        label = { Text("reps") },
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                        singleLine = true,
                        modifier = Modifier.width(80.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Checkbox(
                        checked = set.completed,
                        onCheckedChange = {
                            workoutSets = workoutSets.toMutableList().also { list ->
                                list[index] = list[index].copy(completed = it)
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    workoutSets = workoutSets.toMutableList().apply {
                        add(WorkoutSet(lbs = workoutSets.toMutableList()[workoutSets.size-1].lbs,
                            reps = workoutSets.toMutableList()[workoutSets.size-1].reps))
                    }
                }
            ) {
                Text("Add Set", color = Color.White) // White text looks good on red
            }
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text("Cancel Workout", color = Color.White) // White text looks good on red
            }
        }
    }
}

data class WorkoutSet(
    var lbs: Int = 0,
    var reps: Int = 0,
    var completed: Boolean = false
)

@Composable
fun HeaderText(text: String){
    Text(text, fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
}

@Preview(showBackground = true)
@Composable
fun GreetingWorkoutScreen() {
    FitnessWithFriendsTheme {
        WorkoutScreen(Workout(name = "Push Ups", duration = "20", image_url = "234", description = "This is a description",), navController = rememberNavController(), user = User())
    }
}