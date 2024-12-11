package com.example.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            appNavigation()
        }
    }
}

@Composable
fun appNavigation() {
    // Firebase must be initialized before this
    val viewModel = GameModel()
    viewModel.initGame()

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "player") {
        composable("player") { Player(navController, viewModel) }
        composable("lobby") { Lobby(navController, viewModel) }
        composable("game/{gameId}") { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId")
            Game(navController, viewModel, gameId)
        }
    }
}
