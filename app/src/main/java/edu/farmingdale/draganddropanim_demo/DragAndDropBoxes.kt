@file:OptIn(ExperimentalFoundationApi::class)

package edu.farmingdale.draganddropanim_demo

import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope


@Composable
fun DragAndDropBoxes(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize()) {

        Row( // decide weight
            modifier = modifier
                .fillMaxWidth()
                .weight(0.2f)
        ) {
            val boxCount = 4
            var dragBoxIndex by remember {
                mutableIntStateOf(0)
            }

            repeat(boxCount) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(10.dp)
                        .border(1.dp, Color.Black)
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { event ->
                                event
                                    .mimeTypes()
                                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            },
                            target = remember {
                                object : DragAndDropTarget {
                                    override fun onDrop(event: DragAndDropEvent): Boolean {

                                        dragBoxIndex = index //destination
                                        return true
                                    }
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    this@Row.AnimatedVisibility(
                        visible = index == dragBoxIndex, // == Checking, Easy way, just changing visibility
                        enter = scaleIn() + fadeIn(), // Use when to make more complicated
                        exit = scaleOut() + fadeOut()
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxSize()
                                .dragAndDropSource {
                                    detectTapGestures( // detect in modifier specific long press
                                        onLongPress = { offset -> // Start
                                            startTransfer( // Define what we do
                                                transferData = DragAndDropTransferData(
                                                    clipData = ClipData.newPlainText( //ctrl c copy
                                                        "text",
                                                        "" // "if"
                                                    )
                                                )
                                            )
                                        }
                                    )
                                }
                        )
                    }
                }
            }
        }

        val rotateAnimation = remember { Animatable(0f) }
        val verticalMovement = remember { Animatable(100f) }
        val horizontalMovement = remember { Animatable(100f) }
        var resetPosition by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            rotateAnimation.animateTo(
                720f,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
            )
            verticalMovement.animateTo(
                600f,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
            )
            horizontalMovement.animateTo(
                600f,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
            )

        }
        LaunchedEffect (resetPosition){
            horizontalMovement.animateTo(100f, animationSpec = tween(durationMillis = 1000))
            verticalMovement.animateTo(100f, animationSpec = tween(durationMillis = 1000))
            resetPosition = false
        }
        // Button to reset the rectangle back to the center
        Button(
            onClick = {
                resetPosition = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Reset to Original Spot")
        }
        Box(
            modifier = Modifier.graphicsLayer {
                alpha = rotateAnimation.value
            }
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .background(Color.Red)

        ) {
//          drawCircle(Color.Green, radius = 50f, center = Offset(100f, 100f)
            rotate(rotateAnimation.value, pivot = Offset(150f, 150f)) {
                translate(left = horizontalMovement.value, top = verticalMovement.value) {
                    drawRect(Color.Green, size = Size(100f, 100f))
                }
            }
        }
    }
}

