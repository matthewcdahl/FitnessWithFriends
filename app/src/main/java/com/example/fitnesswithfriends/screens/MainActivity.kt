package com.example.fitnesswithfriends.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fitnesswithfriends.ui.theme.FitnessWithFriendsTheme
import com.example.fitnesswithfriends.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnesswithfriends.models.User
import com.example.fitnesswithfriends.firebase.GoogleSignInClient
import com.example.fitnesswithfriends.viewmodels.WorkoutsViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {



    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val googleAuthClient = GoogleSignInClient(applicationContext)

        enableEdgeToEdge()
        setContent {
            FitnessWithFriendsTheme {

                var isSignedIn by rememberSaveable{mutableStateOf(false)}

                Scaffold(modifier = Modifier.fillMaxSize()) {  innerPadding ->
                    if(isSignedIn) {
                        MainScreen(user = googleAuthClient.getCurrentUser())
                    }
                    else{
                        GoogleSignInScreen {
                            lifecycleScope.launch {
                                isSignedIn = googleAuthClient.signIn()
                                println("SIGNED IN: " + isSignedIn)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleSignInScreen(onSignInClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top spacer to push image downward
        Spacer(modifier = Modifier.weight(1f))

        // Your image roughly at the 1/3 mark
        Image(
            painter = painterResource(R.drawable.fitness),
            contentDescription = "Fitness Logo",
            modifier = Modifier.scale(2f)
        )

        // Spacer between image and button
        Spacer(modifier = Modifier.weight(1f))

        // Button section
        Button(
            onClick = onSignInClick,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(2.dp, Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google_icon),
                contentDescription = "Google Sign In",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Sign in with Google")
        }

        // Bottom spacer to fill remaining space
        Spacer(modifier = Modifier.weight(2f))
    }
}

@Composable
fun MainScreen(user: User) {
    val navController = rememberNavController()
    val items = BottomNavItem.allItems
    val workoutViewModel: WorkoutsViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {


                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            when (val icon = item.icon) {
                                is ImageVector -> {
                                    // Use predefined ImageVector
                                    Icon(imageVector = icon, contentDescription = item.label)
                                }
                                is Int -> {
                                    // Use custom drawable from resources as a Painter
                                    val painter: Painter = painterResource(id = icon)
                                    Icon(painter = painter, contentDescription = item.label)
                                }
                            }
                        },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    // Avoid building up a huge back stack:
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = BottomNavItem.Workouts.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Profile.route) {
                Profile(user = user, isVisible = currentRoute == BottomNavItem.Profile.route)
            }
            composable(BottomNavItem.Workouts.route) { Workouts(workoutViewModel, navController = navController) }
            composable(BottomNavItem.Users.route) { Users(name = "Users") }
            composable("workout") {
                val workout = workoutViewModel.grabSelectedWorkout()
                WorkoutScreen(workout, navController, user)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

sealed class BottomNavItem(val route: String, val label: String, val icon: Any) {

    object Users : BottomNavItem("users", "Users", R.drawable.group_24px)
    object Workouts : BottomNavItem("workouts", "Workouts", R.drawable.fitness_center_24px)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)

    companion object {
        val allItems = listOf(Users, Workouts, Profile)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FitnessWithFriendsTheme {
        GoogleSignInScreen(onSignInClick = {

        })
    }
}