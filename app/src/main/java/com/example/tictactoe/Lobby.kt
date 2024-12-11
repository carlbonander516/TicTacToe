package com.example.tictactoe

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.asStateFlow

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Lobby(navController: NavController, viewModel: GameModel) {
    val players by viewModel.playerMap.asStateFlow().collectAsStateWithLifecycle()
    val games by viewModel.gameMap.asStateFlow().collectAsStateWithLifecycle()

    LaunchedEffect(games) {
        games.forEach { (gameId, game) ->
            if ((game.player1Id == viewModel.localPlayerId.value || game.player2Id == viewModel.localPlayerId.value) &&
                (game.gameState in listOf("player1_turn", "player2_turn"))
            ) {
                navController.navigate("game/$gameId")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lobby", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5))
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            items(players.entries.toList()) { entry ->
                val (id, player) = entry
                if (id != viewModel.localPlayerId.value) {
                    ListItem(
                        headlineContent = { Text(player.name) },
                        trailingContent = {
                            Button(onClick = { viewModel.createGame(id) }) {
                                Text("Challenge")
                            }
                        }
                    )
                }
            }
        }
    }
}
