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


@OptIn(ExperimentalMaterial3Api::class)
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

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFE1F5FE)) { // Soft blue background
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "TicTacToe",
                style = MaterialTheme.typography.headlineLarge.copy(color = Color(0xFF01579B)) // Deep Blue
            )

            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = { Text("Enter your name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF0288D1),
                    unfocusedBorderColor = Color(0xFF81D4FA)
                )
            )

            Button(
                onClick = {
                    if (playerName.isNotBlank()) {
                        viewModel.addPlayer(playerName) { playerId ->
                            Log.d("PlayerScreen", "Player added with ID: $playerId")
                            sharedPreferences.edit().putString("playerId", playerId).apply()
                            viewModel.localPlayerId.value = playerId // Ensure localPlayerId is set
                            navController.navigate("lobby")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)) // Blue
            ) {
                Text("Enter Game", color = Color.White)
            }
        }
    }
}

