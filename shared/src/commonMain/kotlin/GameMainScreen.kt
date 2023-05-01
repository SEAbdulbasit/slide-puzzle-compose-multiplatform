import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
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

/**
 * Created by abdulbasit on 30/04/2023.
 */

@Composable
fun PuzzleBoard(value: Int) {
    val boxWidth = remember { mutableStateOf(-1f) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        StarsBackground { boxWidth.value = it - 16 }
        MeteoroidAnimation(Modifier)
        PuzzleTiles(value, boxWidth.value)
    }
}

@Composable
fun PuzzleTiles(puzzleSize: Int, boxWidth: Float) {
    val numTiles = puzzleSize * puzzleSize
    val tilePositions =
        remember(key1 = puzzleSize) { mutableStateListOf(*(1 until numTiles).toList().toTypedArray(), 0) }
    val emptyTilePosition = remember { mutableStateOf(numTiles - 1) }

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

            if (isPuzzleSolved()) {
                //TODO add dialog when puzzle solved
            }
        }
    }

    val tileSize = remember(boxWidth) { boxWidth / puzzleSize }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn {
            items(puzzleSize) { row ->
                LazyRow {
                    items(puzzleSize) { col ->
                        val position = row * puzzleSize + col
                        val tile = tilePositions[position]
                        val isTileInRightPos = isTileInRightPosition(position)
                        Box(Modifier.padding(4.dp).border(
                            border = BorderStroke(
                                width = 2.dp,
                                color = Color.White.copy(alpha = 0.6f),
                            ), shape = RoundedCornerShape(16.dp)
                        ).width((tileSize / puzzleSize).dp).aspectRatio(1f).background(
                            color = if (isTileInRightPos) Color.Gray.copy(0.5f) else Color.White.copy(
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
fun StarsBackground(updateBoxWidth: (Float) -> Unit) {
    val stars = remember { mutableStateListOf<Star>() }

    LaunchedEffect(true) {

        // Add stars to the list
        repeat(200) {
            stars.add(
                Star(
                    x = Random.nextFloat(),
                    y = Random.nextFloat(),
                    radius = Random.nextFloat().coerceIn(3f, 10f),
                    brightness = Random.nextFloat().coerceIn(0.1f, 0.3f)
                )
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition()

    // Add moon to the list
    val moon = remember {
        Star(
            x = 0f, y = 0.4f, radius = 80f, brightness = 1f
        )
    }

    // Animate moon position
    val moonPosition by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        )
    )

    moon.x = moonPosition

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
        val brightness by infiniteTransition.animateFloat(
            initialValue = star.brightness, targetValue = 1f - star.brightness, animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = Random.nextInt(1000, 2000)), repeatMode = RepeatMode.Reverse
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

    // Draw the moon
    Canvas(modifier = Modifier.fillMaxSize()) {
        updateBoxWidth(this.size.width)
        drawCircle(
            color = Color.White, radius = moon.radius, center = Offset(moon.x * this.size.width, moon.radius * 4)
        )
    }
}

@Composable
fun MeteoroidAnimation(modifier: Modifier) {
    val meteoroids = remember { mutableStateListOf<Meteoroid>() }

    LaunchedEffect(true) {
        // Add meteoroids to the list
        repeat(20) {
            meteoroids.add(
                Meteoroid(
                    x = Random.nextFloat(),
                    y = Random.nextFloat(),
                    size = Random.nextFloat().coerceIn(10f, 20f),
                    speed = Random.nextInt(10, 30).toFloat()
                )
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height


        // Draw the meteoroids
        meteoroids.forEach { meteoroid ->
            // Move the meteoroid
            meteoroid.x += meteoroid.speed / 1000f

            // If the meteoroid is off-screen, reset it
            if (meteoroid.x > 1f) {
                meteoroid.x = -meteoroid.size
                meteoroid.y = Random.nextFloat()
                meteoroid.speed = Random.nextInt(10, 30).toFloat()
            }

            // Draw the meteoroid
            drawCircle(
                color = Color.Gray, radius = meteoroid.size, center = Offset(meteoroid.x * width, meteoroid.y * height)
            )
        }
    }
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