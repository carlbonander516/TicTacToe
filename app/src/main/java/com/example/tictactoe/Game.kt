@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tictactoe

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun Game(navController: NavController, viewModel: GameModel, gameId: String?) {
    val players by viewModel.playerMap.asStateFlow().collectAsStateWithLifecycle()
    val games by viewModel.gameMap.asStateFlow().collectAsStateWithLifecycle()
    val localPlayerId by remember { viewModel.localPlayerId }.collectAsStateWithLifecycle()
    val rows = 3
    val cols = 3

    val playerName = players[localPlayerId]?.name ?: "Player"

    if (gameId != null && games.containsKey(gameId)) {
        val game = games[gameId]!!

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Tic Tac Toe - $playerName",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B5E20)) // Deep Green
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFFE8F5E9)), // Light green background
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (game.gameState) {
                    "player1_won", "player2_won", "draw" -> {
                        Text(
                            "Game Over!",
                            style = MaterialTheme.typography.headlineLarge.copy(color = Color(0xFFD32F2F)) // Red
                        )
                        Spacer(modifier = Modifier.padding(16.dp))

                        val result = when (game.gameState) {
                            "draw" -> "It's a Draw!"
                            "player1_won" -> "Player 1 Wins!"
                            else -> "Player 2 Wins!"
                        }
                        Text(
                            result,
                            style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFF004D40)) // Teal
                        )

                        Button(
                            onClick = { navController.navigate("lobby") },
                            modifier = Modifier.padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)) // Forest Green
                        ) {
                            Text("Return to Lobby", color = Color.White)
                        }
                    }
                    else -> {
                        val isPlayerTurn = (game.gameState == "player1_turn" && game.player1Id == localPlayerId) ||
                                (game.gameState == "player2_turn" && game.player2Id == localPlayerId)
                        Text(
                            if (isPlayerTurn) "Your Turn!" else "Opponent's Turn",
                            style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFF004D40)) // Teal
                        )
                        Spacer(modifier = Modifier.padding(16.dp))

                        for (i in 0 until rows) {
                            Row(horizontalArrangement = Arrangement.Center) {
                                for (j in 0 until cols) {
                                    val index = i * cols + j
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp) // Larger cells
                                            .padding(4.dp)
                                            .border(
                                                width = 6.dp, // Thicker borders
                                                color = Color(0xFFD32F2F) // Red
                                            )
                                    ) {
                                        Button(
                                            onClick = { viewModel.checkGameState(gameId, index) },
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB91)) // Soft orange
                                        ) {
                                            val symbol = when (game.gameBoard[index]) {
                                                1 -> "X"
                                                2 -> "O"
                                                else -> ""
                                            }
                                            Text(
                                                symbol,
                                                color = Color.Black,
                                                style = MaterialTheme.typography.headlineLarge
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        Log.e("Game", "Game not found: $gameId")
        navController.navigate("lobby")
    }
}