package com.laralbir.mdghnavigator.feature.reader.ui.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.laralbir.mdghnavigator.feature.reader.R

class ZoomState(
    val minScale: Float = 1.0f,
    val maxScale: Float = 5.0f
) {
    var scale by mutableFloatStateOf(minScale)
        private set

    var offsetX by mutableFloatStateOf(0.0f)
        private set

    var offsetY by mutableFloatStateOf(0.0f)
        private set

    fun applyTransform(
        zoomMultiplier: Float,
        panX: Float = 0f,
        panY: Float = 0f,
        maxPanWidth: Float = 0f,
        maxPanHeight: Float = 0f
    ) {
        val newScale = (scale * zoomMultiplier).coerceIn(minScale, maxScale)
        scale = newScale

        if (scale <= minScale) {
            offsetX = 0f
            offsetY = 0f
        } else {
            val updatedX = offsetX + panX
            val updatedY = offsetY + panY

            if (maxPanWidth > 0f) {
                val boundX = maxPanWidth * (scale - 1f) / 2f
                offsetX = updatedX.coerceIn(-boundX, boundX)
            } else {
                offsetX = updatedX
            }

            if (maxPanHeight > 0f) {
                val boundY = maxPanHeight * (scale - 1f) / 2f
                offsetY = updatedY.coerceIn(-boundY, boundY)
            } else {
                offsetY = updatedY
            }
        }
    }

    fun onDoubleTap(tapX: Float = 0f, tapY: Float = 0f) {
        if (scale > 1.2f) {
            reset()
        } else {
            scale = 2.5f
            offsetX = 0f
            offsetY = 0f
        }
    }

    fun reset() {
        scale = minScale
        offsetX = 0f
        offsetY = 0f
    }

    fun zoomIn() {
        scale = (scale + 0.5f).coerceAtMost(maxScale)
    }

    fun zoomOut() {
        val newScale = (scale - 0.5f).coerceAtLeast(minScale)
        scale = newScale
        if (scale <= minScale) {
            offsetX = 0f
            offsetY = 0f
        }
    }
}

@Composable
fun rememberZoomState(
    minScale: Float = 1.0f,
    maxScale: Float = 5.0f
): ZoomState = remember { ZoomState(minScale, maxScale) }

/**
 * Modal a pantalla completa para visualización interactiva de imágenes y diagramas.
 * Permite gestos multitáctiles de pellizco para zoom (1x a 5x), doble toque para ampliar
 * o restablecer, desplazamiento (pan) y barra de controles superior táctil.
 */
@Composable
fun ZoomableImageViewer(
    imageUrl: String,
    title: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    zoomState: ZoomState = rememberZoomState()
) {
    val context = LocalContext.current
    var controlsVisible by remember { mutableStateOf(true) }

    val isDiagram = remember(imageUrl) { ImageDataUriParser.isSvgOrDiagram(imageUrl) }
    val resolvedModel = remember(imageUrl) { ImageDataUriParser.resolveImageModel(imageUrl) }

    val imageRequest = remember(resolvedModel) {
        ImageRequest.Builder(context)
            .data(resolvedModel)
            .decoderFactory(SvgDecoder.Factory())
            .crossfade(true)
            .build()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xE6101010))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomState.applyTransform(
                            zoomMultiplier = zoom,
                            panX = pan.x,
                            panY = pan.y
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { offset ->
                            zoomState.onDoubleTap(offset.x, offset.y)
                        },
                        onTap = {
                            controlsVisible = !controlsVisible
                        }
                    )
                }
        ) {
            // Imagen o diagrama central interactivo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = zoomState.scale
                        scaleY = zoomState.scale
                        translationX = zoomState.offsetX
                        translationY = zoomState.offsetY
                    },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = if (isDiagram) Color.White else Color.Transparent,
                    shape = if (isDiagram) RoundedCornerShape(8.dp) else RectangleShape,
                    shadowElevation = if (isDiagram) 8.dp else 0.dp,
                    modifier = if (isDiagram) Modifier.padding(24.dp) else Modifier.fillMaxSize()
                ) {
                    SubcomposeAsyncImage(
                        model = imageRequest,
                        contentDescription = title ?: stringResource(R.string.image_viewer_title_default),
                        contentScale = ContentScale.Fit,
                        loading = {
                            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF0969DA))
                            }
                        },
                        error = {
                            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = stringResource(R.string.image_viewer_error),
                                    color = if (isDiagram) Color(0xFF1F2328) else Color.White
                                )
                            }
                        },
                        modifier = if (isDiagram) Modifier.padding(16.dp) else Modifier.fillMaxSize()
                    )
                }
            }

            // Barra de controles superior translúcida
            if (controlsVisible) {
                Surface(
                    color = Color(0xCC1E1E1E),
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .align(Alignment.TopCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.image_viewer_close),
                                tint = Color.White
                            )
                        }

                        Text(
                            text = title?.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.image_viewer_title_default),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )

                        // Nivel de zoom actual
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x44FFFFFF),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "${(zoomState.scale * 100).toInt()}%",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = { zoomState.zoomOut() },
                            enabled = zoomState.scale > zoomState.minScale
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomOut,
                                contentDescription = stringResource(R.string.image_viewer_zoom_out),
                                tint = if (zoomState.scale > zoomState.minScale) Color.White else Color.Gray
                            )
                        }

                        IconButton(
                            onClick = { zoomState.zoomIn() },
                            enabled = zoomState.scale < zoomState.maxScale
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = stringResource(R.string.image_viewer_zoom_in),
                                tint = if (zoomState.scale < zoomState.maxScale) Color.White else Color.Gray
                            )
                        }

                        IconButton(onClick = { zoomState.reset() }) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = stringResource(R.string.image_viewer_zoom_reset),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

