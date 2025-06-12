package com.mobiledev.randomuserapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mobiledev.randomuserapp.data.db.AppDatabase
import com.mobiledev.randomuserapp.data.db.User
import com.mobiledev.randomuserapp.screens.CreateUserScreen
import com.mobiledev.randomuserapp.screens.HomeScreen
import com.mobiledev.randomuserapp.screens.QRScannerScreen
import com.mobiledev.randomuserapp.screens.SettingsScreen
import com.mobiledev.randomuserapp.screens.UserDetailsScreen
import com.mobiledev.randomuserapp.screens.UserOverviewScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen(navController) }
                composable("user_overview") {
                    val context = LocalContext.current
                    val db = remember { AppDatabase.getInstance(context)}
                    val dao = remember {db.userDao()}

                    val users by produceState(initialValue = emptyList<User>()) {
                        value = dao.getAll()
                    }

                    UserOverviewScreen(
                        navController = navController,
                        users = users,
                        onUserClick = {user ->
                            navController.navigate("user_detail/${user.id}")
                        }
                    )

                }
                composable(
                    "user_detail/{userId}",
                    arguments = listOf(navArgument("userId") {type = NavType.IntType})
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getInt("userId") ?: return@composable
                    val context = LocalContext.current
                    val db = remember { AppDatabase.getInstance(context)}
                    val dao = remember {db.userDao()}

                    var user by remember { mutableStateOf<User?>(null)}

                    LaunchedEffect(userId) {
                        user = dao.getUserById(userId)
                    }

                    user?.let {
                        UserDetailsScreen(navController = navController, userId = it.id)
                    }
                }
                composable("qr_scanner") { QRScannerScreen((navController)) }
                composable("settings") { SettingsScreen(navController) }
                composable("create_user") {
                    val snackbarHostState = remember { SnackbarHostState() }
                    CreateUserScreen(navController, snackbarHostState)
                }
                
            }
        }
    }
}