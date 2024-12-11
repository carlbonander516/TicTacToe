package com.example.tictactoe

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow

data class Player(
    var name: String = ""
)

data class Game(
    var gameBoard: List<Int> = List(9) { 0 }, // 0: empty, 1: player1's move, 2: player2's move
    var gameState: String = "invite", // Possible values: "invite", "player1_turn", "player2_turn", "player1_won", "player2_won", "draw"
    var player1Id: String = "",
    var player2Id: String = ""
)

const val rows = 3
const val cols = 3

class GameModel : ViewModel() {
    val db = Firebase.firestore // Ensure FirebaseApp is initialized before this
    var localPlayerId = MutableStateFlow<String?>(null)
    val playerMap = MutableStateFlow<Map<String, Player>>(emptyMap())
    val gameMap = MutableStateFlow<Map<String, Game>>(emptyMap())

    fun initGame() {
        // Listen for player updates
        db.collection("players").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val updatedMap = snapshot.documents.associate { doc ->
                    doc.id to doc.toObject(Player::class.java)!!
                }
                playerMap.value = updatedMap
            }
        }

        // Listen for game updates
        db.collection("games").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val updatedMap = snapshot.documents.associate { doc ->
                    doc.id to doc.toObject(Game::class.java)!!
                }
                gameMap.value = updatedMap
            }
        }
    }

    fun addPlayer(name: String, callback: (String) -> Unit) {
        val newPlayer = Player(name)
        db.collection("players")
            .add(newPlayer)
            .addOnSuccessListener { documentRef ->
                callback(documentRef.id)
            }
    }

    fun createGame(opponentId: String) {
        val newGame = Game(
            player1Id = localPlayerId.value!!,
            player2Id = opponentId
        )
        db.collection("games").add(newGame)
    }

    fun checkGameState(gameId: String?, cell: Int) {
        if (gameId == null) return

        val game = gameMap.value[gameId] ?: return
        val isMyTurn =
            (game.gameState == "player1_turn" && game.player1Id == localPlayerId.value) ||
                    (game.gameState == "player2_turn" && game.player2Id == localPlayerId.value)

        if (!isMyTurn) return

        val gameBoard = game.gameBoard.toMutableList()
        if (gameBoard[cell] != 0) return

        gameBoard[cell] = if (game.gameState == "player1_turn") 1 else 2
        val nextGameState = when (checkWinner(gameBoard)) {
            1 -> "player1_won"
            2 -> "player2_won"
            3 -> "draw"
            else -> if (game.gameState == "player1_turn") "player2_turn" else "player1_turn"
        }

        db.collection("games").document(gameId).update(
            "gameBoard", gameBoard,
            "gameState", nextGameState
        )
    }

    fun checkWinner(board: List<Int>): Int {
        val winningLines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Columns
            listOf(0, 4, 8), listOf(2, 4, 6)  // Diagonals
        )

        for (line in winningLines) {
            if (board[line[0]] != 0 && board[line[0]] == board[line[1]] && board[line[0]] == board[line[2]]) {
                return board[line[0]]
            }
        }

        return if (board.none { it == 0 }) 3 else 0 // 3 means a draw, 0 means no winner yet
    }
}
