import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun App() {
    MaterialTheme {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            PuzzleGame()
        }
    }
}


@Composable
fun PuzzleGame() {
    val tilePositions = remember { mutableStateListOf(1, 2, 3, 4, 5, 6, 7, 8, 0) }
    val emptyTilePosition = remember { mutableStateOf(8) }

    fun shuffleTiles() {
        // Shuffle the tiles using Fisher-Yates shuffle algorithm
        for (i in 8 downTo 1) {
            val j = (0..i).random()
            tilePositions.swap(i, j)
        }
        emptyTilePosition.value = tilePositions.indexOf(0)
    }

    fun canMoveTile(position: Int): Boolean {
        val emptyPosition = emptyTilePosition.value
        return (position - emptyPosition == 1 && position % 3 != 0) || (emptyPosition - position == 1 && emptyPosition % 3 != 0) || position - emptyPosition == 3 || position - emptyPosition == -3
    }

    fun isPuzzleSolved(): Boolean {
        return tilePositions == listOf(1, 2, 3, 4, 5, 6, 7, 8, 0)
    }

    fun isTileInRightPosition(position: Int): Boolean {
        return tilePositions[position] == position + 1
    }

    fun moveTile(position: Int) {
        if (canMoveTile(position)) {
            val emptyPosition = emptyTilePosition.value
            tilePositions.swap(emptyPosition, position)
            emptyTilePosition.value = position

            if (isPuzzleSolved()) {
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn {
            items(3) { row ->
                LazyRow {
                    items(3) { col ->
                        val position = row * 3 + col
                        val tile = tilePositions[position]
                        val isTileInRightPos = isTileInRightPosition(position)
                        Box(
                            Modifier
                                .padding(4.dp)
                                .border(border = BorderStroke(2.dp, color = Color.DarkGray))
                                .size(100.dp)
                                .background(
                                    color = if (isTileInRightPos) Color.Green else Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .clickable {
                                    moveTile(position)
                                }) {
                            if (tile != 0) {
                                Text(
                                    tile.toString(),
                                    fontSize = 24.sp,
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


fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}