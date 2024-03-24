package ru.hse.numberdetector

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DrawingScreen(
    viewModel: DigitClassifierViewModel = viewModel(
        factory = ClassifierViewModelFactory(LocalContext.current)
    )
) {
    val minVal =
        minOf(
            LocalConfiguration.current.screenWidthDp,
            LocalConfiguration.current.screenHeightDp
        )
    val width = with(LocalDensity.current) { minVal.dp.toPx() }

    LaunchedEffect(viewModel.initialized) {
        if (!viewModel.initialized) {
            viewModel.initialize(width)
        }
    }
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .background(Color.White)
                .size(minVal.dp, minVal.dp)
                .pointerInput(true) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val line = DigitClassifierViewModel.Line(
                            start = change.position - dragAmount,
                            end = change.position
                        )
                        viewModel.addLine(line)
                    }
                }
        ) {
            drawImage(viewModel.imageBitmap)
        }
        Text(text = viewModel.prediction, style = TextStyle(color = MaterialTheme.colorScheme.background))
        Button(onClick = { viewModel.clearImage() }) {
            Text(text = "Очистить")
        }
    }
}
