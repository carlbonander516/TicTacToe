@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tictactoe

import android.util.Log
import androidx.compose.foundation.background
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

    val playerName = players[localPlayerId]?.name ?: "Player"

    if (gameId != null && games.containsKey(gameId)) {
        val game = games[gameId]!!

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Tic Tac Toe - $playerName", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5))
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFFB3E5FC)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (game.gameState) {
                    "player1_won", "player2_won", "draw" -> {
                        Text("Game Over!", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.padding(16.dp))

                        val result = when (game.gameState) {
                            "draw" -> "It's a Draw!"
                            "player1_won" -> "Player 1 Wins!"
                            else -> "Player 2 Wins!"
                        }
                        Text(result, style = MaterialTheme.typography.headlineSmall, color = Color.Red)

                        Button(onClick = { navController.navigate("lobby") }) {
                            Text("Return to Lobby")
                        }
                    }
                    else -> {
                        val isPlayerTurn = (game.gameState == "player1_turn" && game.player1Id == localPlayerId) ||
                                (game.gameState == "player2_turn" && game.player2Id == localPlayerId)
                        Text(
                            if (isPlayerTurn) "Your Turn!" else "Opponent's Turn",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.padding(16.dp))
                        for (i in 0 until rows) {
                            Row(horizontalArrangement = Arrangement.Center) {
                                for (j in 0 until cols) {
                                    val index = i * cols + j
                                    Button(
                                        onClick = { viewModel.checkGameState(gameId, index) },
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(4.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                                    ) {
                                        val symbol = when (game.gameBoard[index]) {
                                            1 -> "X"
                                            2 -> "O"
                                            else -> ""
                                        }
                                        Text(symbol, color = Color.White, style = MaterialTheme.typography.headlineSmall)
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
