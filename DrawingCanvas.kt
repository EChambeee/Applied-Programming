package com.example.paintkotlin

import androidx.compose.ui.graphics.toArgb
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.applyCanvas
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

data class PathWithPaint(
    val path: Path,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun DrawingCanvas() {
    val context = LocalContext.current
    val paths = remember { mutableStateListOf<PathWithPaint>() }
    val undonePaths = remember { mutableStateListOf<PathWithPaint>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var brushColor by remember { mutableStateOf(Color.Black) }
    var brushSize by remember { mutableStateOf(8f) }

    fun saveDrawing() {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        bitmap.applyCanvas {
            drawColor(android.graphics.Color.WHITE)
            paths.forEach { item ->
                val paint = android.graphics.Paint().apply {
                    color = item.color.toArgb()
                    strokeWidth = item.strokeWidth
                    style = android.graphics.Paint.Style.STROKE
                    isAntiAlias = true
                }
                drawPath(item.path.asAndroidPath(), paint)
            }
        }

        val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val fileName = "drawing_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.png"
        val file = File(picturesDir, fileName)

        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Toast.makeText(context, "Saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (paths.isNotEmpty()) {
                    val last = paths.last()
                    paths.remove(last)
                    undonePaths.add(last)
                }
            }) { Text("Undo") }

            Button(onClick = {
                if (undonePaths.isNotEmpty()) {
                    val last = undonePaths.last()
                    undonePaths.remove(last)
                    paths.add(last)
                }
            }) { Text("Redo") }

            Button(onClick = {
                paths.clear()
                undonePaths.clear()
            }) { Text("Clear") }

            Button(onClick = { saveDrawing() }) {
                Text("Save")
            }
        }

        // Color selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(Color.Black, Color.Red, Color.Blue, Color.Green, Color.Magenta).forEach { color ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color)
                        .clickable { brushColor = color } // ← Fixed: tap to change color
                )
            }
        }

        // Drawing canvas
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath = Path().apply { moveTo(offset.x, offset.y) }
                        },
                        onDrag = { change, _ ->
                            currentPath?.lineTo(change.position.x, change.position.y)
                        },
                        onDragEnd = {
                            currentPath?.let {
                                paths.add(PathWithPaint(it, brushColor, brushSize))
                                currentPath = null
                            }
                        },
                        onDragCancel = { currentPath = null }
                    )
                }
        ) {
            // Draw all saved paths
            paths.forEach { item ->
                drawPath(
                    path = item.path,
                    color = item.color,
                    style = Stroke(width = item.strokeWidth)
                )
            }

            // Draw current path in progress
            currentPath?.let {
                drawPath(
                    path = it,
                    color = brushColor,
                    style = Stroke(width = brushSize)
                )
            }
        }
    }
}
