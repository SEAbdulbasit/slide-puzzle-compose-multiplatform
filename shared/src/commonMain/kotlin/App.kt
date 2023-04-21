import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun App() {
    MaterialTheme {
        Box(
            Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            StarsBackground()
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
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        LazyColumn {
            items(3) { row ->
                LazyRow {
                    items(3) { col ->
                        val position = row * 3 + col
                        val tile = tilePositions[position]
                        val isTileInRightPos = isTileInRightPosition(position)
                        Box(Modifier.padding(4.dp)
                            .border(
                                border = BorderStroke(
                                    2.dp,
                                    color = Color.White.copy(alpha = 0.6f),
                                ),
                                shape = RoundedCornerShape(5.dp)
                            )
                            .size(100.dp).background(
                                color = if (isTileInRightPos) Color.Gray.copy(0.5f) else Color.White.copy(
                                    alpha = 0.1f
                                ),
                                shape = RoundedCornerShape(8.dp),
                            )
                            .clickable {
                                moveTile(position)
                            }) {
                            if (tile != 0) {
                                Text(
                                    text = tile.toString(),
                                    color = Color.White,
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

@Composable
fun StarsBackground() {
    val stars = remember { mutableStateListOf<Star>() }

    LaunchedEffect(true) {
        // Add stars to the list
        repeat(200) {
            stars.add(
                Star(
                    x = Random.nextFloat(),
                    y = Random.nextFloat(),
                    radius = Random.nextFloat().coerceIn(3f, 10f),
                    brightness = Random.nextFloat().coerceIn(0.2f, 1f)
                )
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Draw a background color gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF202840),
                    Color(0xFF0A0E17),
                    Color(0xFF02030A),
                ), startY = 0f, endY = height
            ), size = Size(width, height)
        )
    }

    // Draw the stars
    stars.forEach { star ->
        val infiniteTransition = rememberInfiniteTransition()
        val brightness by infiniteTransition.animateFloat(
            initialValue = star.brightness,
            targetValue = 1f - star.brightness,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = Random.nextInt(1000, 3000)),
                repeatMode = RepeatMode.Reverse
            )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            drawCircle(
                color = Color.White.copy(alpha = brightness),
                radius = star.radius,
                center = Offset(star.x * width, star.y * height)
            )
        }
    }
}

data class Star(
    var x: Float, var y: Float, var radius: Float, var brightness: Float
)


fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}