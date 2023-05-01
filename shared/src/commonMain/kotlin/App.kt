import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    MaterialTheme {
        val isSideMenuOpen = remember { mutableStateOf(false) }
        val selectedPuzzleSize = remember { mutableStateOf(3) }
        Box(
            Modifier.fillMaxSize()
        ) {
            PuzzleBoard(selectedPuzzleSize.value)
            Box(
                Modifier.padding(16.dp).border(
                    border = BorderStroke(
                        2.dp,
                        color = Color.White.copy(alpha = 0.6f),
                    ), shape = RoundedCornerShape(16.dp)
                )
            ) {
                Icon(imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(16.dp).size(24.dp).clickable {
                        isSideMenuOpen.value = true
                    })
            }
            if (isSideMenuOpen.value) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(top = 32.dp, bottom = 32.dp, end = 32.dp).border(
                            shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp), border = BorderStroke(
                                width = 2.dp,
                                color = Color.White.copy(alpha = 0.6f),
                            )
                        ).background(
                            color = Color(0xFF202840).copy(alpha = 0.9f),
                            shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                        ).clipToBounds()

                    ) {

                        Column {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Slide Puzzle",
                                    style = MaterialTheme.typography.h5.copy(Color.White),
                                    modifier = Modifier.padding(16.dp).align(Alignment.TopStart)

                                )
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(top = 18.dp, end = 16.dp).size(24.dp).clickable {
                                        isSideMenuOpen.value = false
                                    }.align(Alignment.TopEnd)
                                )
                            }
                            Text(
                                text = "Puzzle Size",
                                style = MaterialTheme.typography.body1.copy(color = Color.White.copy(alpha = 0.8f)),
                                modifier = Modifier.padding(
                                    top = 16.dp, start = 16.dp
                                )
                            )
                            Text(
                                text = "Select the size of your puzzle",
                                style = MaterialTheme.typography.body1.copy(color = Color.White.copy(alpha = 0.8f)),
                                modifier = Modifier.padding(
                                    top = 16.dp, start = 16.dp
                                )
                            )
                            PuzzleSizeBoxOptions(selectedPuzzleSize = selectedPuzzleSize.value, onPuzzleSizeChanged = {
                                selectedPuzzleSize.value = it
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PuzzleSizeBoxOptions(onPuzzleSizeChanged: (Int) -> Unit, selectedPuzzleSize: Int) {
    val puzzleSizes = listOf(3, 4, 5, 6)
    LazyRow(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 16.dp, end = 16.dp).clipToBounds()) {
        items(items = puzzleSizes, itemContent = {
            PuzzleSizeBox(it, selectedPuzzleSize, onPuzzleSizeChanged)
        })
    }
}

@Composable
fun PuzzleSizeBox(puzzleSize: Int, selectedPuzzleSize: Int, onPuzzleSizeChanged: (Int) -> Unit) {
    Column {
        Box(Modifier.padding(end = 8.dp).border(
            border = BorderStroke(
                2.dp,
                color = Color.White.copy(alpha = 0.6f),
            ), shape = RoundedCornerShape(8.dp)
        ).background(
            color = if (puzzleSize == selectedPuzzleSize) Color.White else Color(0xFF202840),
            shape = RoundedCornerShape(8.dp),
        ).clickable {
            onPuzzleSizeChanged(puzzleSize)
        }) {
            Text(
                text = "$puzzleSize x $puzzleSize",
                color = if (puzzleSize == selectedPuzzleSize) Color(0xFF202840) else Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center).padding(16.dp)
            )
        }
        Text(
            text = "${puzzleSize * puzzleSize - 1} tiles",
            color = Color.White,
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
        )
    }
}
