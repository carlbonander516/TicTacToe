package com.example.tictactoe

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Player(navController: NavController, viewModel: GameModel) {
    val sharedPreferences = LocalContext.current.getSharedPreferences("TicTacToePrefs", Context.MODE_PRIVATE)
    var playerName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val playerId = sharedPreferences.getString("playerId", null)
        if (playerId != null) {
            viewModel.localPlayerId.value = playerId
            navController.navigate("lobby")
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFB3E5FC)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to Tic Tac Toe!", style = MaterialTheme.typography.headlineLarge)

            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = { Text("Enter your name") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (playerName.isNotBlank()) {
                        Log.d("PlayerScreen", "Attempting to add player: $playerName")
                        viewModel.addPlayer(playerName) { playerId ->
                            Log.d("PlayerScreen", "Player added with ID: $playerId")
                            sharedPreferences.edit().putString("playerId", playerId).apply()
                            navController.navigate("lobby")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Start")
            }
        }
    }
}
