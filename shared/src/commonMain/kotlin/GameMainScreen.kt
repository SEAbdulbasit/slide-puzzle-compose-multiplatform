import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import background.MeteoroidAnimation
import background.StarsBackground

/**
 * Created by abdulbasit on 30/04/2023.
 */

@Composable
fun PuzzleBoard(value: Int) {
    val boxWidth = remember { mutableStateOf(-1f) }
    StarsBackground { boxWidth.value = it }
    MeteoroidAnimation(Modifier)
    PuzzleGame(value, boxWidth.value)
}

@Composable
fun PuzzleGame(puzzleSize: Int, boxWidth: Float) {

    val moves = remember { mutableStateOf(0) }

    val numTiles = puzzleSize * puzzleSize
    val tilePositions = remember(key1 = puzzleSize) {
        mutableStateListOf(
            *(1 until numTiles).toList().toTypedArray(), 0
        )
    }
    val emptyTilePosition = remember { mutableStateOf(numTiles - 1) }
    val correctTiles = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 96.dp, start = 16.dp, end = 16.dp),
    ) {
        Text(
            text = "Puzzle Slide",
            style = MaterialTheme.typography.h4,
            color = Color.White,
        )
        Text(
            text = "Solve this Puzzle ",
            style = MaterialTheme.typography.body1,
            color = Color.White,
        )
        Row {
            Text(
                text = "Time",
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.W700,
                color = Color.White,
            )
            Text(
                text = "Moves:",
                style = MaterialTheme.typography.body1,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = "${moves.value}",
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.W700,
                color = Color.White,
            )
            Text(
                text = "Corrected tiles:",
                style = MaterialTheme.typography.body1,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = "${correctTiles.value}",
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.W700,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        PuzzleTiles(
            puzzleSize,
            boxWidth,
            numTiles,
            tilePositions,
            emptyTilePosition,
            correctTiles,
            moves
        )
    }
}

@Composable
fun PuzzleTiles(
    puzzleSize: Int,
    boxWidth: Float,
    numTiles: Int,
    tilePositions: SnapshotStateList<Int>,
    emptyTilePosition: MutableState<Int>,
    correctTiles: MutableState<Int>,
    moves: MutableState<Int>,
) {

    fun shuffleTiles() {

        // Shuffle the tiles using Fisher-Yates shuffle algorithm
        for (i in numTiles - 1 downTo 1) {
            val j = (0..i).random()
            tilePositions.swap(i, j)
        }
        emptyTilePosition.value = tilePositions.indexOf(0)
    }

    LaunchedEffect(puzzleSize) {
        shuffleTiles()
    }

    fun canMoveTile(position: Int): Boolean {
        val emptyPosition = emptyTilePosition.value
        val row = position / puzzleSize
        val col = position % puzzleSize
        val emptyRow = emptyPosition / puzzleSize
        val emptyCol = emptyPosition % puzzleSize
        return (row == emptyRow && col == emptyCol - 1) || // Left
                (row == emptyRow && col == emptyCol + 1) || // Right
                (row == emptyRow - 1 && col == emptyCol) || // Up
                (row == emptyRow + 1 && col == emptyCol)    // Down
    }

    fun isPuzzleSolved(): Boolean {
        return tilePositions == (1 until numTiles).toList() + listOf(0)
    }

    fun isTileInRightPosition(position: Int): Boolean {
        return tilePositions[position] == position + 1
    }

    fun moveTile(position: Int) {
        if (canMoveTile(position)) {
            val emptyPosition = emptyTilePosition.value
            tilePositions.swap(emptyPosition, position)
            emptyTilePosition.value = position
            correctTiles.value = countElementsInCorrectPosition(tilePositions)
            moves.value = moves.value + 1

            if (isPuzzleSolved()) {
                //TODO add dialog when puzzle solved
            }
        }
    }

    val tileSize = remember(boxWidth) { boxWidth / puzzleSize }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn {
            items(puzzleSize) { row ->
                LazyRow {
                    items(puzzleSize) { col ->
                        val position = row * puzzleSize + col
                        val tile = tilePositions[position]
                        val isTileInRightPos = isTileInRightPosition(position)
                        val isEmptyTile = tile == 0
                        Box(Modifier.padding(4.dp).border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isEmptyTile) Color.Transparent else Color.White.copy(
                                    alpha = 0.6f
                                ),
                            ), shape = RoundedCornerShape(16.dp)
                        ).width(((tileSize / puzzleSize)).dp).aspectRatio(1f).background(
                            color = if (isEmptyTile) Color.Transparent
                            else if (isTileInRightPos) Color.Gray.copy(
                                0.5f
                            ) else Color.White.copy(
                                alpha = 0.1f
                            ),
                            shape = RoundedCornerShape(16.dp),
                        ).clickable {
                            moveTile(position)
                        }) {
                            if (tile != 0) {
                                Text(
                                    text = tile.toString(),
                                    color = Color.White,
                                    fontSize = (tileSize / puzzleSize * 0.3).sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        shuffleTiles()
    }
}

fun countElementsInCorrectPosition(list: List<Int>): Int {
    var count = 0

    for (index in list.indices) {
        val expectedValue = index + 1
        if (list.getOrNull(index) == expectedValue) {
            count++
        }
    }

    return count
}

data class Meteoroid(
    var x: Float, var y: Float, val size: Float, var speed: Float
)

data class Star(
    var x: Float, var y: Float, var radius: Float, var brightness: Float
)

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}