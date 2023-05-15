package background

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.random.Random


/**
 * Created by abdulbasit on 14/05/2023.
 */


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
                color = Color.Gray,
                radius = meteoroid.size,
                center = Offset(meteoroid.x * width, meteoroid.y * height)
            )
        }
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
            initialValue = star.brightness,
            targetValue = 1f - star.brightness,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = Random.nextInt(1000, 2000)),
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

    // Draw the moon
    Canvas(modifier = Modifier.fillMaxSize()) {
        updateBoxWidth(this.size.width)
        drawCircle(
            color = Color.White,
            radius = moon.radius,
            center = Offset(moon.x * this.size.width, moon.radius * 4)
        )
    }
}

data class Meteoroid(
    var x: Float, var y: Float, val size: Float, var speed: Float
)

data class Star(
    var x: Float, var y: Float, var radius: Float, var brightness: Float
)
